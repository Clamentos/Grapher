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
import java.util.List;

///..
import java.util.concurrent.ThreadLocalRandom;

///..
import java.util.stream.Collectors;

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
// TODO: proper password strength validation
public class UserService {

    ///
    private final UserRepository repository;
    private final AuditRepository auditRepository;
    private final OperationRepository operationRepository;
    private final UserMapper mapper;
    private final OperationMapper operationMapper;
    private final SessionService sessionService;

    ///..
    private final int effort;

    ///
    @Autowired
    public UserService(

        UserRepository repository,
        AuditRepository auditRepository,
        OperationRepository operationRepository,
        UserMapper mapper,
        OperationMapper operationMapper,
        SessionService sessionService,

        @Value("${grapher-auth.bcryptEffort}") int effort
    ) {

        this.repository = repository;
        this.auditRepository = auditRepository;
        this.operationRepository = operationRepository;
        this.mapper = mapper;
        this.operationMapper = operationMapper;
        this.sessionService = sessionService;

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
            user.setId(ThreadLocalRandom.current().nextLong());

            user = repository.save(user);
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

            if(BCrypt.verifyer().verify(credentials.getPassword().toCharArray(), entity.getPassword()).verified == true) {

                List<OperationDto> operations = operationMapper.mapIntoDtos(operationRepository.findAllByUser(entity));
                UserDto user = mapper.mapIntoDto(entity);

                user.setPassword(null);
                user.setOperations(operations);

                String sessionId = sessionService.generate(
                    
                    user.getId(),
                    user.getUsername(),
                    user.getOperations().stream().map(OperationDto::getId).collect(Collectors.toSet())
                );

                return(new AuthDto(user, sessionId));
            }

            else {

                throw new AuthenticationException(ErrorFactory.generate(ErrorCode.WRONG_PASSWORD));
            }
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.USER_NOT_FOUND, credentials.getUsername()));
        }
    }

    ///..
    public void logout(String sessionId) {

        sessionService.remove(sessionId);
    }

    ///..
    public List<UserDto> getAllUsers(String username, String email, String createdAtRange, String updatedAtRange, String operations)
    throws IllegalArgumentException {

        List<Long> dates = parse(createdAtRange, updatedAtRange);

        return(mapper.mapIntoDtos(repository.findAll(new UserSpecification(

            username,
            email,
            dates.get(0),
            dates.get(1),
            dates.get(2),
            dates.get(3),
            operations.length() == 0 ? null : operations.split(",")
        ))));
    }

    ///..
    @Transactional
    public UserDto getUserById(long id) throws EntityNotFoundException {

        User entity = repository.findById(id);

        if(entity != null) {

            UserDto user = mapper.mapIntoDto(entity);

            user.setPassword(null);
            user.setOperations(operationMapper.mapIntoDtos(operationRepository.findAllByUser(entity)));

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

                    Validator.requireFilled(user.getUsername(), "username");

                    if(repository.existsByUsername(user.getUsername()) == false) {

                        entity.setUsername(user.getUsername());
                        columns.append("username,");
                    }

                    else {

                        throw new EntityExistsException(ErrorFactory.generate(ErrorCode.USER_ALREADY_EXISTS, user.getUsername()));
                    }
                }

                if(user.getPassword() != null) {

                    Validator.requireFilled(user.getPassword(), "password");
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

                    if(entity.getId() != requestingUserId) {

                        List<Operation> operations = operationRepository.findAllByName(

                            user.getOperations()
                                .stream()
                                .map(OperationDto::getName)
                                .toList()
                        );

                        operations.forEach(o -> userOperations.add(new UserOperation(0, entity, o)));
                        entity.setOperations(userOperations);
                        columns.append("operations,");
                    }

                    else {

                        throw new AuthorizationException(ErrorFactory.generate(ErrorCode.ILLEGAL_ACTION));
                    }
                }

                entity.getInstantAudit().setUpdatedAt(System.currentTimeMillis());
                entity.getInstantAudit().setUpdatedBy(username);

                columns.deleteCharAt(columns.length() - 1);

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
    private List<Long> parse(String createdAtRange, String updatedAtRange) throws IllegalArgumentException {

        List<Long> dates = new ArrayList<>();

        dates.addAll(parseSingle(createdAtRange));
        dates.addAll(parseSingle(updatedAtRange));

        return(dates);
    }

    ///..
    private List<Long> parseSingle(String dateRange) throws IllegalArgumentException {

        List<Long> dates = new ArrayList<>();

        if(dateRange.length() > 0) {

            String[] splits = dateRange.split(",");

            try {

                if(splits.length == 1) dates.add(Long.parseLong(splits[1]));
                if(splits.length == 2) dates.add(Long.parseLong(splits[2]));
            }

            catch(NumberFormatException exc) {

                throw new IllegalArgumentException(ErrorFactory.generate(ErrorCode.BAD_FORMAT, splits[1]));
            }
        }

        else {

            dates.add(null);
            dates.add(null);
        }

        return(dates);
    }

    ///
}
