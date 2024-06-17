package io.github.clamentos.grapher.auth.business.services;

///
import at.favre.lib.crypto.bcrypt.BCrypt;

///.
import io.github.clamentos.grapher.auth.business.mappers.OperationMapper;
import io.github.clamentos.grapher.auth.business.mappers.UserMapper;

///..
import io.github.clamentos.grapher.auth.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.exceptions.AuthorizationException;
import io.github.clamentos.grapher.auth.exceptions.ValidationException;

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
import io.github.clamentos.grapher.auth.utility.AuditUtils;
import io.github.clamentos.grapher.auth.utility.ErrorCode;
import io.github.clamentos.grapher.auth.utility.ErrorFactory;
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
import jakarta.transaction.Transactional;

///.
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
    public void register(UserDto userDetails) throws ValidationException {

        Validator.validateAuditedObject(userDetails);
        Validator.requireNull(userDetails.getId(), "id");
        Validator.requireFilled(userDetails.getUsername(), "username");
        Validator.requireFilled(userDetails.getPassword(), "password");
        Validator.requireNotNull(userDetails.getEmail(), "email");
        Validator.requireNull(userDetails.getFlags(), "flags");
        Validator.requireNull(userDetails.getOperations(), "operations");

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

    ///..
    @Transactional
    public AuthDto login(UsernamePassword credentials) throws AuthenticationException, EntityNotFoundException, SecurityException {

        User entity = repository.findFullByUsername(credentials.getUsername());

        if(entity == null) {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.ENTITY_NOT_FOUND, credentials.getUsername()));
        }

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
        claims.put("operations", user.getOperations());

        String token = tokenService.generate(claims);
        return(new AuthDto(user, token));
    }

    ///..
    public List<UserDto> getAllUsers() {

        return(mapper.mapIntoDtos(repository.findAll()));
    }

    ///..
    @Transactional
    public UserDto getUserById(long id) {

        User entity = repository.findFullById(id);

        if(entity != null) {

            List<OperationDto> operations = operationMapper.mapIntoDtos(operationRepository.findAllByUser(entity));
            UserDto user = mapper.mapIntoDto(entity);

            user.setPassword(null);
            user.setOperations(operations);

            return(user);
        }

        throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.ENTITY_NOT_FOUND));
    }

    ///..
    @Transactional
    public void updateUser(String username, long requestingUserId, UserDto user, boolean canModifyOthers)
    throws AuthorizationException, EntityExistsException, EntityNotFoundException, ValidationException {

        Validator.validateAuditedObject(user);
        Validator.requireNotNull(user.getId(), "id");

        User entity = repository.findById(user.getId()).get();

        if(entity != null) {

            if(entity.getId() == requestingUserId || (entity.getId() != requestingUserId && canModifyOthers)) {

                StringBuilder columns = new StringBuilder();

                if(user.getUsername() != null) {

                    if(repository.existsByUsername(user.getUsername())) {

                        throw new EntityExistsException(ErrorFactory.generate(ErrorCode.ENTITY_ALREADY_EXISTS, user.getUsername()));
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

                        List<Operation> operations = operationRepository.findAllByNames(

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

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.ENTITY_NOT_FOUND));
        }
    }

    ///..
    @Transactional
    public void deleteUser(String username, long id) throws EntityNotFoundException {

        User entity = repository.findById(id).get();

        if(entity != null) {

            auditRepository.saveAll(AuditUtils.deleteUserAudit(entity, System.currentTimeMillis(), username));
            repository.delete(entity);
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.ENTITY_NOT_FOUND));
        }
    }

    ///
}
