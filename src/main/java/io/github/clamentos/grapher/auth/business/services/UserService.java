package io.github.clamentos.grapher.auth.business.services;

///
import at.favre.lib.crypto.bcrypt.BCrypt;

///.
import io.github.clamentos.grapher.auth.business.Action;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;
import io.github.clamentos.grapher.auth.error.exceptions.IllegalActionException;
import io.github.clamentos.grapher.auth.error.exceptions.NotificationException;
import io.github.clamentos.grapher.auth.error.exceptions.WrongPasswordException;

///..
import io.github.clamentos.grapher.auth.persistence.AuditAction;
import io.github.clamentos.grapher.auth.persistence.LockReason;
import io.github.clamentos.grapher.auth.persistence.UserRole;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Session;
import io.github.clamentos.grapher.auth.persistence.entities.Subscription;
import io.github.clamentos.grapher.auth.persistence.entities.User;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.UserRepository;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.ForgotPasswordDto;
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UserSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernameEmailDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePasswordDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.core.env.Environment;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.data.domain.PageRequest;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
/**
 * <h3>User Service</h3>
 * Spring {@link Service} to manage {@link User} objects.
*/

///
@Service
@Slf4j

///
public class UserService {

    ///
    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final SessionService sessionService;
    private final ValidatorService validatorService;

    ///..
    private final int bcryptEffort;
    private final int loginFailures;
    private final long userLockTime;
    private final long passwordExpirationTime;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public UserService(

        UserRepository userRepository,
        AuditRepository auditRepository,
        SessionService sessionService,
        ValidatorService validatorService,
        Environment environment
    ) {

        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
        this.sessionService = sessionService;
        this.validatorService = validatorService;

        bcryptEffort = environment.getProperty("grapher-auth.bcryptEffort", Integer.class, 12);
        loginFailures = environment.getProperty("grapher-auth.loginFailures", Integer.class, 5);
        userLockTime = environment.getProperty("grapher-auth.userLockTime", Long.class, 60_000L);
        passwordExpirationTime = environment.getProperty("grapher-auth.passwordExpirationTime", Long.class, 5_184_000_000L);
    }

    ///
    /**
     * Registers a new user with the specified details.
     * @param session : The session of the calling user, if available.
     * @param userDetails : The user details.
     * @throws DataAccessException If any database access errors occur.
     * @throws EntityExistsException If the specified user already exists.
     * @throws IllegalArgumentException If {@code userDetails} fails validation.
    */
    @Transactional
    public void register(Session session, UserDto userDetails)
    throws DataAccessException, EntityExistsException, IllegalArgumentException {

        validatorService.validateAndSanitize(userDetails, false);
        if(session != null) sessionService.check(session.getId(), Action.CREATE_OTHERS.getAllowedRoles(), null, "Cannot create others");

        if(userRepository.existsByUsername(userDetails.getUsername())) {

            throw new EntityExistsException(ErrorFactory.create(

                ErrorCode.USER_ALREADY_EXISTS, "UserService::register -> User already exists", userDetails.getUsername()
            ));
        }

        User user = userRepository.save(new User(

            userDetails.getUsername(),
            BCrypt.withDefaults().hashToString(bcryptEffort, userDetails.getPassword().toCharArray()),
            userDetails.getEmail(),
            userDetails.getProfilePicture() != null ? Base64.getDecoder().decode(userDetails.getProfilePicture()) : null,
            userDetails.getAbout(),
            session != null ? session.getUsername() : userDetails.getUsername()
        ));

        auditRepository.save(new Audit(user, AuditAction.CREATED, user.getCreatedBy(), null));
    }

    ///..
    /**
     * Logs the calling user in.
     * @param credentials : The user's credentials.
     * @return The never {@code null} login details.
     * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code credentials} doesn't pass validation.
    */
    @Transactional(noRollbackFor = WrongPasswordException.class)
    public AuthDto login(UsernamePasswordDto credentials)
    throws AuthenticationException, AuthorizationException, DataAccessException, IllegalArgumentException {

        validatorService.validateCredentials(credentials);
        User user = userRepository.findFullByUsername(credentials.getUsername());

        if(user == null) {

            throw new AuthenticationException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "UserService::login -> User not found", credentials.getUsername()
            ));
        }

        long now = System.currentTimeMillis();

        if(user.getLockedUntil() >= now) {

            throw new AuthorizationException(ErrorFactory.create(

                ErrorCode.USER_LOCKED, "UserService::login -> User is locked", user.getLockedUntil(), user.getLockReason()
            ));
        }

        if(user.getPasswordLastChangedAt() + passwordExpirationTime < now) {

            throw new AuthorizationException(ErrorFactory.create(

                ErrorCode.USER_PASSWORD_EXPIRED, "UserService::login -> User password expired", user.getPasswordLastChangedAt()
            ));
        }

        if(BCrypt.verifyer().verify(credentials.getPassword().toCharArray(), user.getPassword()).verified) {

            user.setFailedAccesses((short)0);
            userRepository.save(user);

            Session session = sessionService.generate(user.getId(), user.getUsername(), user.getRole());

            log.info("User {} logged in", user.getId());
            return(new AuthDto(this.map(user, true), session.getId(), session.getExpiresAt()));
        }

        else {

            if(user.getFailedAccesses() > loginFailures) {

                user.setLockedUntil(now + (userLockTime * (2 << (user.getFailedAccesses() - loginFailures))));
                user.setLockReason(LockReason.TOO_MANY_FAILED_LOGINS.name());

                sessionService.removeAll(user.getId());
            }

            user.setFailedAccesses((short)(user.getFailedAccesses() + 1));
            userRepository.save(user);

            log.info("User {} failed to login", user.getId());
            throw new WrongPasswordException(ErrorFactory.create(ErrorCode.WRONG_PASSWORD, "UserService::login -> Wrong password"));
        }
    }

    ///..
    /**
     * Logs the calling user out of the specified session.
     * @param session : The session of the calling user.
     * @throws AuthenticationException If authentication fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    public void logout(Session session) throws AuthenticationException, DataAccessException, NullPointerException {

        sessionService.remove(session.getId());
        log.info("User {} logged out from single session", session.getUserId());
    }

    ///..
    /**
     * Logs the calling user out of all sessions.
     * @param session : The session of the calling user.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    public void logoutAll(Session session) throws NullPointerException {

        sessionService.removeAll(session.getUserId());
        log.info("User {} logged out from all sessions", session.getUserId());
    }

    ///..
    /**
     * Gets all the users that match the provided search filter.
     * @param session : The session of the calling user.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of minimal user details.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    public List<UserDto> getAllUsersByFilter(Session session, UserSearchFilterDto searchFilter)
    throws DataAccessException, IllegalArgumentException, NullPointerException {

        validatorService.validateSearchFilter(searchFilter);
        sessionService.check(session.getId(), Action.SEARCH_USERS.getAllowedRoles(), null, "Cannot search users");

        PageRequest pageRequest = PageRequest.of(searchFilter.getPageNumber(), searchFilter.getPageSize());
        List<User> fetchedUsers;

        if(searchFilter.getUsernameLike() != null && searchFilter.getEmailLike() != null) {

            fetchedUsers = userRepository.findAllMinimalByUsernameAndEmail(

                searchFilter.getUsernameLike(),
                searchFilter.getEmailLike(),
                pageRequest
            );
        }

        else if(searchFilter.getUsernameLike() != null) {

            fetchedUsers = userRepository.findAllMinimalByUsername(searchFilter.getUsernameLike(), pageRequest);
        }

        else if(searchFilter.getEmailLike() != null) {

            fetchedUsers = userRepository.findAllMinimalByEmail(searchFilter.getEmailLike(), pageRequest);
        }

        else if(searchFilter.getSubscribedToNames() == null) {

            fetchedUsers = userRepository.findAllMinimalByFilters(

                searchFilter.getRoles(),
                searchFilter.getCreatedAtStart(),
                searchFilter.getCreatedAtEnd(),
                searchFilter.getUpdatedAtStart(),
                searchFilter.getUpdatedAtEnd(),
                searchFilter.getCreatedByNames(),
                searchFilter.getUpdatedByNames(),
                searchFilter.getLockedCheckMode(),
                System.currentTimeMillis(),
                searchFilter.getExpiredPasswordCheckMode(),
                passwordExpirationTime,
                searchFilter.getFailedAccesses(),
                pageRequest
            );
        }

        else {

            fetchedUsers = userRepository.findAllMinimalByFilters(

                searchFilter.getSubscribedToNames(),
                searchFilter.getRoles(),
                searchFilter.getCreatedAtStart(),
                searchFilter.getCreatedAtEnd(),
                searchFilter.getUpdatedAtStart(),
                searchFilter.getUpdatedAtEnd(),
                searchFilter.getCreatedByNames(),
                searchFilter.getUpdatedByNames(),
                searchFilter.getLockedCheckMode(),
                System.currentTimeMillis(),
                searchFilter.getExpiredPasswordCheckMode(),
                passwordExpirationTime,
                searchFilter.getFailedAccesses(),
                pageRequest
            );
        }

        return(fetchedUsers.stream().map(this::mapMinimal).toList());
    }

    ///..
    /**
     * Gets the details of the specified user.
     * @param session : The session of the calling user.
     * @param id : The id of the target user.
     * @return The never {@code null} user details.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If {@code id} doesn't match to any user.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    public UserDto getUserById(Session session, long id)
    throws DataAccessException, EntityNotFoundException, NullPointerException {

        User user = userRepository.findFullById(id);

        if(user == null) {

            throw new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "UserService::getUserById -> User not found", id
            ));
        }

        return(this.map(user, Action.SEE_PRIVATE_OTHERS.getAllowedRoles().contains(session.getUserRole()) || session.getUserId() == id));
    }

    ///..
    /**
     * Updates the specified user with the provided parameters.
     * @param session : The session of the calling user.
     * @param userDetails : The new user details to be merged with the existing ones.
     * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If the target user doesn't exist.
     * @throws IllegalActionException If the calling user attempts to perform an illegal action.
     * @throws IllegalArgumentException If {@code userDetails} doesn't pass validation.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    @Transactional
    public void updateUser(Session session, UserDto userDetails) throws AuthenticationException, AuthorizationException,
    DataAccessException, EntityNotFoundException, IllegalActionException, IllegalArgumentException, NullPointerException {

        validatorService.validateAndSanitize(userDetails, true);

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() ->

            new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "UserService::updateUser -> User not found", userDetails.getId()
            ))
        );

        boolean isNotSelf = session.getUserId() != user.getId();
        long now = System.currentTimeMillis();

        if(!isNotSelf && user.getLockedUntil() >= now) {

            throw new AuthorizationException(ErrorFactory.create(

                ErrorCode.USER_LOCKED,
                "UserService::updateUser -> User is locked",
                user.getLockedUntil(),
                user.getLockReason()
            ));
        }

        this.checkActionsForUpdate(userDetails, isNotSelf);

        boolean roleChanged = false;
        boolean userChanged = false;
        boolean passwordChanged = false;
        StringBuilder updatedColumns = new StringBuilder();

        if(userDetails.getPassword() != null) {

            validatorService.validatePassword(userDetails.getPassword(), "password");

            if(BCrypt.verifyer().verify(userDetails.getPassword().toCharArray(), user.getPassword()).verified) {

                throw new IllegalActionException(ErrorFactory.create(

                    ErrorCode.ILLEGAL_ACTION_SAME_PASSWORD, "UserService::updateUser -> New password is equal to old password"
                ));
            }

            user.setPassword(BCrypt.withDefaults().hashToString(bcryptEffort, userDetails.getPassword().toCharArray()));
            user.setPasswordLastChangedAt(now);

            userChanged = true;
            passwordChanged = true;

            updatedColumns.append("password,password_last_changed_at");
        }

        if(userDetails.getEmail() != null) {

            validatorService.validateEmail(userDetails.getEmail(), "email");

            user.setEmail(userDetails.getEmail());

            userChanged = true;
            updatedColumns.append("email,");
        }

        if(userDetails.getAbout() != null) {

            user.setAbout(userDetails.getAbout());

            userChanged = true;
            updatedColumns.append("about,");
        }

        if(userDetails.getRole() != null) {

            sessionService.check(

                session.getId(),
                Action.CHANGE_ROLE_OTHERS.getAllowedRoles(),
                user.getRole(),
                "Cannot change the role"
            );

            user.setRole(userDetails.getRole());

            userChanged = true;
            roleChanged = true;

            updatedColumns.append("role,");
        }

        if(userDetails.getLockedUntil() != null) {

            sessionService.check(

                session.getId(),
                Action.CHANGE_LOCK_DATE_OTHERS.getAllowedRoles(),
                user.getRole(),
                "Cannot change the lock date"
            );

            validatorService.requireNotNull(userDetails.getLockReason(), "lockReason");

            user.setLockedUntil(userDetails.getLockedUntil());
            user.setLockReason(LockReason.CUSTOM.name() + "|" + userDetails.getLockReason());

            userChanged = true;
            updatedColumns.append("locked_until,lock_reason");
        }

        if(userChanged) {

            String username = session.getUsername();

            user.setUpdatedAt(now);
            user.setUpdatedBy(username);
            user = userRepository.save(user);

            updatedColumns.deleteCharAt(updatedColumns.length() - 1);
            auditRepository.save(new Audit(user, AuditAction.UPDATED, username, updatedColumns.toString()));

            if(roleChanged && !passwordChanged) sessionService.updateRole(session.getUserId(), user.getRole());
            if(passwordChanged) sessionService.removeAll(session.getUserId());
        }
    }

    ///..
    /**
     * Deletes the specified user and removes all of its sessions.
     * @param session : The session of the calling user.
     * @param id : The id of the target user.
     * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If the target user is not found.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    @Transactional
    public void deleteUser(Session session, long id)
    throws AuthenticationException, AuthorizationException, DataAccessException, EntityNotFoundException, NullPointerException {

        if(session.getUserId() != id) {

            sessionService.check(

                session.getId(),
                Action.DELETE_OTHERS.getAllowedRoles(),
                UserRole.ADMINISTRATOR,
                "Cannot delete others"
            );
        }

        User user = userRepository.findById(id).orElseThrow(() ->

            new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "UserService::deleteUser -> User not found", id
            ))
        );

        sessionService.removeAll(id);
        userRepository.delete(user);
        auditRepository.save(new Audit(user, AuditAction.DELETED, session.getUsername(), null));
    }

    ///..
    /**
     * Starts a forgot password session.
     * @param usernameEmail : The user details.
     * @throws AuthorizationException If authorization fails.
     * @throws IllegalArgumentException If {@code usernameEmail} doesn't pass validation.
     * @throws NotificationException If the notification cannot be sent to the message broker.
    */
    public void startForgotPassword(UsernameEmailDto usernameEmail)
    throws AuthorizationException, IllegalArgumentException, NotificationException {

        validatorService.validateUsernameEmail(usernameEmail);
        User user = userRepository.findFullByUsername(usernameEmail.getUsername());

        if(user == null) {

            throw new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "UserService::startForgotPassword -> User not found", user
            ));
        }

        sessionService.generateForgotPassword(usernameEmail.getUsername(), usernameEmail.getEmail());
    }

    ///..
    /**
     * Ends a forgot password session and applies the changes.
     * @param forgotPassword : The forgot password details.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If the target user is not found.
     * @throws IllegalArgumentException If {@code forgotPassword} doesn't pass validation.
    */
    @Transactional
    public void endForgotPassword(ForgotPasswordDto forgotPassword)
    throws AuthorizationException, DataAccessException, EntityNotFoundException, IllegalArgumentException {

        validatorService.validateForgotPasswordDto(forgotPassword);

        String username = sessionService.confirmForgotPassword(forgotPassword.getForgotPasswordSessionId());
        User user = userRepository.findFullByUsername(username);

        if(user == null) {

            throw new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "UserService::endForgotPassword -> User not found", username
            ));
        }

        user.setPassword(BCrypt.withDefaults().hashToString(bcryptEffort, forgotPassword.getForgotPasswordSessionId().toCharArray()));
        user.setPasswordLastChangedAt(System.currentTimeMillis());

        userRepository.save(user);
        auditRepository.save(new Audit(user, AuditAction.UPDATED, username, "password,password_last_changed_at"));

        sessionService.removeAll(user.getId());
    }

    ///.
    private UserDto map(User user, boolean privateMode) throws NullPointerException {

        List<SubscriptionDto> subscriptions = new ArrayList<>(user.getSubscriptions().size());

        for(Subscription subscription : user.getSubscriptions()) {

            subscriptions.add(new SubscriptionDto(

                subscription.getId(),
                subscription.getPublisher().getId(),
                subscription.isNotify(),
                subscription.getCreatedAt(),
                subscription.getUpdatedAt()
            ));
        }

        return(new UserDto(

            user.getId(),
            user.getUsername(),
            null,
            privateMode ? user.getEmail() : null,
            user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null,
            user.getAbout(),
            user.getRole(),
            privateMode ? user.getFailedAccesses() : null,
            privateMode ? user.getLockedUntil() : null,
            privateMode ? user.getLockReason() : null,
            privateMode ? user.getPasswordLastChangedAt() : null,
            user.getCreatedAt(),
            user.getCreatedBy(),
            user.getUpdatedAt(),
            user.getUpdatedBy(),
            user.getRole().compareTo(UserRole.CREATOR) >= 0 ? userRepository.countSubscribers(user.getId()) : null,
            privateMode ? subscriptions : null
        ));
    }

    ///..
    private UserDto mapMinimal(User user) throws NullPointerException {

        return(new UserDto(

            user.getId(),
            user.getUsername(),
            null,
            user.getEmail(),
            null,
            null,
            user.getRole(),
            user.getFailedAccesses(),
            user.getLockedUntil(),
            user.getLockReason(),
            user.getPasswordLastChangedAt(),
            user.getCreatedAt(),
            user.getCreatedBy(),
            user.getUpdatedAt(),
            user.getUpdatedBy(),
            -1L,
            null
        ));
    }

    ///..
    private void checkActionsForUpdate(UserDto userDto, boolean isNotSelf) throws IllegalActionException {

        if(userDto.getPassword() != null && isNotSelf) {

            throw new IllegalActionException(ErrorFactory.create(

                ErrorCode.ILLEGAL_ACTION_DIFFERENT_USER, "UserService::updateUser -> Cannot change the password of others"
            ));
        }

        if(userDto.getEmail() != null && isNotSelf) {

            throw new IllegalActionException(ErrorFactory.create(

                ErrorCode.ILLEGAL_ACTION_DIFFERENT_USER, "UserService::updateUser -> Cannot change the email of others"
            ));
        }

        if(userDto.getAbout() != null && isNotSelf) {

            throw new IllegalActionException(ErrorFactory.create(

                ErrorCode.ILLEGAL_ACTION_DIFFERENT_USER, "UserService::updateUser -> Cannot change the about of others"
            ));
        }

        if(userDto.getRole() != null && !isNotSelf) {

            throw new IllegalActionException(ErrorFactory.create(

                ErrorCode.ILLEGAL_ACTION_SAME_USER, "UserService::updateUser -> Cannot change the role of self"
            ));
        }

        if(userDto.getLockedUntil() != null && !isNotSelf) {

            throw new IllegalActionException(ErrorFactory.create(

                ErrorCode.ILLEGAL_ACTION_SAME_USER, "UserService::updateUser -> Cannot change the lock date of self"
            ));
        }
    }

    ///
}
