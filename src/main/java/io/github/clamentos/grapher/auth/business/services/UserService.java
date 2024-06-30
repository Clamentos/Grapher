package io.github.clamentos.grapher.auth.business.services;

///
import at.favre.lib.crypto.bcrypt.BCrypt;

///.
import io.github.clamentos.grapher.auth.business.mappers.OperationMapper;
import io.github.clamentos.grapher.auth.business.mappers.UserMapper;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.InstantAudit;
import io.github.clamentos.grapher.auth.persistence.entities.Operation;
import io.github.clamentos.grapher.auth.persistence.entities.User;
import io.github.clamentos.grapher.auth.persistence.entities.UserOperation;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.OperationRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.UserRepository;

///..
import io.github.clamentos.grapher.auth.persistence.specifications.UserSpecification;

///..
import io.github.clamentos.grapher.auth.utility.AuditUtils;
import io.github.clamentos.grapher.auth.utility.Validator;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.OperationDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///..
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///..
import java.util.concurrent.ThreadLocalRandom;

///.
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

///.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
@Service

///
public class UserService {

    ///
    private final Logger logger;

    ///..
    private final UserRepository repository;
    private final AuditRepository auditRepository;
    private final OperationRepository operationRepository;
    private final UserMapper mapper;
    private final OperationMapper operationMapper;
    private final TokenService tokenService;

    ///..
    private final long tokenDuration;
    private final int effort;

    ///
    @Autowired
    public UserService(

        UserRepository repository,
        AuditRepository auditRepository,
        OperationRepository operationRepository,
        UserMapper mapper,
        OperationMapper operationMapper,
        TokenService tokenService,

        @Value("${grapher-auth.tokenDuration}") long tokenDuration,
        @Value("${grapher-auth.bcryptEffort}") int effort
    ) {

        logger = LogManager.getLogger(this.getClass().getSimpleName());

        this.repository = repository;
        this.auditRepository = auditRepository;
        this.operationRepository = operationRepository;
        this.mapper = mapper;
        this.operationMapper = operationMapper;
        this.tokenService = tokenService;

        this.tokenDuration = tokenDuration;
        this.effort = effort;
    }

    ///
    @Transactional
    public void register(UserDto userDetails) throws EntityExistsException, IllegalArgumentException {

        Validator.validateAuditedObject(userDetails);
        Validator.requireNull(userDetails.getId(), "id");
        Validator.requireFilled(userDetails.getUsername(), "username");
        Validator.requireFilled(userDetails.getPassword(), "password");
        Validator.requireNotNull(userDetails.getEmail(), "email");
        Validator.requireNull(userDetails.getFlags(), "flags");
        Validator.requireNull(userDetails.getOperations(), "operations");

        if(repository.existsByUsername(userDetails.getUsername()) == false) {

            User user = mapper.mapIntoEntity(userDetails);
            long now = System.currentTimeMillis();

            user.setPassword(BCrypt.withDefaults().hashToString(effort, userDetails.getPassword().toCharArray()));
            user.setInstantAudit(new InstantAudit(0, now, now, userDetails.getUsername(), userDetails.getUsername()));

            boolean inserted = false;

            while(inserted == false) {

                try {

                    user.setId(ThreadLocalRandom.current().nextLong());
                    user = repository.save(user);

                    inserted = true;
                }

                catch(EntityExistsException exc) {

                    logger.warn("Insert collision for id: {}, retrying...", user.getId());
                }
            }

            auditRepository.saveAll(AuditUtils.insertUserAudit(user));
        }

        else {

            throw new EntityExistsException(ErrorFactory.generate(ErrorCode.USER_ALREADY_EXISTS, userDetails.getUsername()));
        }
    }

    ///..
    @Transactional
    public AuthDto login(UsernamePassword credentials)
    throws AuthenticationException, EntityNotFoundException, IllegalArgumentException, SecurityException {

        Validator.requireFilled(credentials.getUsername(), "username");
        Validator.requireFilled(credentials.getPassword(), "password");

        User entity = repository.findByUsername(credentials.getUsername());

        if(entity != null) {

            if(BCrypt.verifyer().verify(credentials.getPassword().toCharArray(), entity.getPassword()).verified == false) {

                throw new AuthenticationException(ErrorFactory.generate(ErrorCode.WRONG_PASSWORD));
            }

            List<OperationDto> operations = operationMapper.mapIntoDtos(operationRepository.findAllByUser(entity));
            UserDto user = mapper.mapIntoDto(entity);

            user.setPassword(null);
            user.setOperations(operations);

            Map<String, Object> claims = new HashMap<>();
            long now = System.currentTimeMillis();

            claims.put("jti", ThreadLocalRandom.current().nextLong());
            claims.put("iat", now);
            claims.put("exp", now + tokenDuration);
            claims.put("sub", user.getId());
            claims.put("name", user.getUsername());
            claims.put("flags", user.getFlags());
            claims.put("operations", user.getOperations().stream().map(OperationDto::getId).toList());

            String token = tokenService.generate(claims);
            return(new AuthDto(user, token));
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.USER_NOT_FOUND, credentials.getUsername()));
        }
    }

    ///..
    public void logout(String token) {

        tokenService.blacklistToken(token);
    }

    ///..
    public List<UserDto> getAllUsers(String username, String email, String createdAtRange, String updatedAtRange, String operations) {

        List<Long> dates = parse(createdAtRange, updatedAtRange);

        return(mapper.mapIntoDtos(repository.findAll(new UserSpecification(

            username,
            email,
            dates.get(0),
            dates.get(1),
            dates.get(2),
            dates.get(3),
            operations.equals("") ? null : operations.split(",")
        ))));
    }

    ///..
    @Transactional
    public UserDto getUserById(long id) throws EntityNotFoundException {

        User entity = repository.findById(id);

        if(entity != null) {

            List<OperationDto> operations = operationMapper.mapIntoDtos(operationRepository.findAllByUser(entity));
            UserDto user = mapper.mapIntoDto(entity);

            user.setPassword(null);
            user.setOperations(operations);

            return(user);
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.USER_NOT_FOUND));
        }
    }

    ///..
    @Transactional
    public void updateUser(String username, long requestingUserId, UserDto user, boolean canModifyOthers)
    throws AuthorizationException, EntityExistsException, EntityNotFoundException, IllegalArgumentException {

        Validator.validateAuditedObject(user);
        Validator.requireNotNull(user.getId(), "id");

        User entity = repository.findById((long)user.getId());

        if(entity != null) {

            if(entity.getId() == requestingUserId || (entity.getId() != requestingUserId && canModifyOthers)) {

                StringBuilder columns = new StringBuilder();

                if(user.getUsername() != null) {

                    if(repository.existsByUsername(user.getUsername())) {

                        throw new EntityExistsException(ErrorFactory.generate(ErrorCode.USER_ALREADY_EXISTS, user.getUsername()));
                    }

                    entity.setUsername(user.getUsername());
                    columns.append("username,");
                }

                if(user.getPassword() != null && user.getPassword().length() > 0) {

                    entity.setPassword(BCrypt.withDefaults().hashToString(effort, user.getPassword().toCharArray()));
                    columns.append("password,");
                }

                if(user.getEmail() != null) {
                    
                    entity.setEmail(user.getEmail());
                    columns.append("email,");
                }

                if(user.getFlags() != null) {

                    entity.setFlags(user.getFlags());
                    columns.append("flags,");
                }

                List<UserOperation> userOperations = new ArrayList<>();

                if(user.getOperations() != null) {

                    if(entity.getId() != requestingUserId ) {

                        List<Operation> operations = operationRepository.findAllByName(

                            user.getOperations()
                                .stream()
                                .map(OperationDto::getName)
                                .toList()
                        );

                        for(Operation operation : operations) {

                            userOperations.add(new UserOperation(0, entity, operation));
                        }

                        entity.setOperations(userOperations);
                        columns.append("operations,");
                    }

                    else {

                        throw new AuthorizationException(ErrorFactory.generate(ErrorCode.ILLEGAL_ACTION));
                    }
                }

                columns.deleteCharAt(columns.length() - 1);

                entity.getInstantAudit().setUpdatedAt(System.currentTimeMillis());
                entity.getInstantAudit().setUpdatedBy(username);

                repository.save(entity);
                auditRepository.saveAll(AuditUtils.updateUserAudit(entity, userOperations, columns.toString()));
            }

            else {

                throw new AuthorizationException(ErrorFactory.generate(ErrorCode.ILLEGAL_ACTION));
            }
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.USER_NOT_FOUND, Long.toString(user.getId())));
        }
    }

    ///..
    @Transactional
    public void deleteUser(String username, long id) throws EntityNotFoundException {

        User entity = repository.findByIdMinimal(id);

        if(entity != null) {

            auditRepository.saveAll(AuditUtils.deleteUserAudit(entity, System.currentTimeMillis(), username));
            repository.delete(entity);
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.USER_NOT_FOUND, Long.toString(id)));
        }
    }

    ///.
    private List<Long> parse(String createdAtRange, String updatedAtRange) {

        List<Long> dates = new ArrayList<>();

        dates.addAll(parseSingle(createdAtRange));
        dates.addAll(parseSingle(updatedAtRange));

        return(dates);
    }

    ///..
    private List<Long> parseSingle(String dateRange) throws IllegalArgumentException {

        List<Long> dates = new ArrayList<>();

        if(dateRange.equals("")) {

            dates.add(null);
            dates.add(null);
        }

        else {

            String[] splits = dateRange.split(",");

            try {

                if(splits.length == 1) dates.add(Long.parseLong(splits[1]));
                if(splits.length == 2) dates.add(Long.parseLong(splits[2]));
            }

            catch(NumberFormatException exc) {

                throw new IllegalArgumentException(ErrorFactory.generate(ErrorCode.BAD_FORMAT, splits[1]));
            }
        }

        return(dates);
    }

    ///
}
