package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthRequest;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.ForgotPasswordDto;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UserSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernameEmailDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePasswordDto;

///.
import java.util.Collection;

///..
import java.util.regex.Pattern;

///.
import org.springframework.stereotype.Service;

///
/**
 * <h3>Validator Service</h3>
 * Spring {@link Service} that provides DTO validation methods.
*/

///
@Service

///
public class ValidatorService {

    ///
    private static final String PREFIX = "subscriptions[";

    ///..
    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;
    private final int maxImageSize;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    public ValidatorService() {

        usernamePattern = Pattern.compile("^[a-zA-Z0-9-_.]{3,32}$");
        emailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        passwordPattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=?!])(?=\\S+$).{10,32}$");
        maxImageSize = 275252; // ceil(256*256*3*1.4)
    }

    ///
    /**
     * Checks if the input object is null.
     * @param obj : The target object.
     * @param name : The name of the object.
     * @throws IllegalArgumentException If {@code obj} is not {@code null}.
    */
    public void requireNull(Object obj, String name) throws IllegalArgumentException {

        if(obj != null) throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NULL, "", name));
    }

    ///..
    /**
     * Checks if the input collection is null or empty.
     * @param coll : The target collection.
     * @param name : The name of the collection.
     * @throws IllegalArgumentException If {@code coll} is filled.
    */
    public void requireNullOrEmpty(Collection<?> coll, String name) throws IllegalArgumentException {

        if(coll != null && !coll.isEmpty())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NULL_OR_EMPTY, "", name));
    }

    ///..
    /**
     * Checks if the input collection is null or filled.
     * @param coll : The target collection.
     * @param name : The name of the collection.
     * @throws IllegalArgumentException If {@code coll} is empty.
    */
    public void requireNullOrFilled(Collection<?> coll, String name) throws IllegalArgumentException {

        if(coll != null && coll.isEmpty())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NULL_OR_FILLED, "", name));
    }

    ///..
    /**
     * Checks if the input object is not null.
     * @param obj : The target object.
     * @param name : The name of the object.
     * @throws IllegalArgumentException If {@code obj} is {@code null}.
    */
    public void requireNotNull(Object obj, String name) throws IllegalArgumentException {

        if(obj == null) throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NOT_NULL, "", name));
    }

    ///..
    /**
     * Checks if the input string is filled.
     * @param str : The target string.
     * @param name : The name of the string.
     * @throws IllegalArgumentException If {@code str} is {@code null} or empty.
    */
    public void requireFilled(String str, String name) throws IllegalArgumentException {

        if(str == null || str.length() == 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));
    }

    ///..
    /**
     * Checks if the input collection is filled.
     * @param coll : The target collection.
     * @param name : The name of the collection.
     * @throws IllegalArgumentException If {@code coll} is not filled.
    */
    public void requireFilled(Collection<?> coll, String name) throws IllegalArgumentException {

        if(coll == null || coll.isEmpty())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));
    }

    ///..
    /**
     * Checks if the input collection is filled as well as its elements.
     * @param coll : The target collection.
     * @param name : The name of the collection.
     * @param elemName : The name of the elements.
     * @throws IllegalArgumentException If {@code coll} is not filled or one of its elements is {@code null}.
    */
    public void requireFullyFilled(Collection<?> coll, String name, String elemName) throws IllegalArgumentException {

        if(coll == null || coll.isEmpty())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));

        for(Object elem : coll) this.requireNotNull(elem, elemName);
    }

    ///..
    /**
     * Validates a username with the following regex: ^[a-zA-Z0-9-_.]{3,32}$.
     * @param username : The target username.
     * @param name : The name of the field.
     * @throws IllegalArgumentException If {@code username} doesn't pass validation.
    */
    public void validateUsername(String username, String name) throws IllegalArgumentException {

        if(username == null || username.length() == 0 || !usernamePattern.matcher(username).matches())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_BAD_USERNAME, "", name));
    }

    ///..
    /**
     * Validates a password with the following regex: ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=?!])(?=\\S+$).{10,32}$.
     * @param password : The target password.
     * @param name : The name of the field.
     * @throws IllegalArgumentException If {@code password} doesn't pass validation.
    */
    public void validatePassword(String password, String name) throws IllegalArgumentException {

        if(password == null || password.length() == 0 || !passwordPattern.matcher(password).matches())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_PASSWORD_TOO_WEAK, "", name));
    }

    ///..
    /**
     * Validates an email with the following regex: ^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$.
     * @param email : The target email.
     * @param name : The name of the field.
     * @throws IllegalArgumentException If {@code email} doesn't pass validation.
    */
    public void validateEmail(String email, String name) throws IllegalArgumentException {

        if(email != null && email.length() > 0 && !emailPattern.matcher(email).matches())
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_BAD_EMAIL, "", name));
    }

    ///..
    /**
     * Validates and sanitizes the provided user details for registration or update.
     * @param userDetails : The target user details.
     * @param isUpdate : {@code true} if update mode, {@code false} otherwise.
     * @throws IllegalArgumentException If {@code userDetails} doesn't pass validation.
    */
    public void validateAndSanitize(UserDto userDetails, boolean isUpdate) throws IllegalArgumentException {

        this.requireNotNull(userDetails, "userDetails");

        if(isUpdate) {

            this.requireNotNull(userDetails.getId(), "userDetails.id");
            this.requireNull(userDetails.getUsername(), "userDetails.username");
            this.requireNull(userDetails.getFailedAccesses(), "userDetails.failedAccesses");
            this.requireNull(userDetails.getPasswordLastChangedAt(), "userDetails.passwordLastChangedAt");
        }

        else {

            this.requireNull(userDetails.getId(), "userDetails.id");
            this.validateUsername(userDetails.getUsername(), "userDetails.username");
            this.validatePassword(userDetails.getPassword(), "userDetails.password");
            this.validateEmail(userDetails.getEmail(), "userDetails.email");
            this.requireNull(userDetails.getRole(), "userDetails.role");
            this.requireNull(userDetails.getFailedAccesses(), "userDetails.failedAccesses");
            this.requireNull(userDetails.getLockedUntil(), "userDetails.lockedUntil");
            this.requireNull(userDetails.getPasswordLastChangedAt(), "userDetails.passwordLastChangedAt");

            if(userDetails.getEmail() == null) userDetails.setEmail("");
            if(userDetails.getAbout() == null) userDetails.setAbout("");
            if(userDetails.getPreferences() == null) userDetails.setPreferences("");

            if(userDetails.getProfilePicture() != null && userDetails.getProfilePicture().length() > maxImageSize) {

                throw new IllegalArgumentException(ErrorFactory.create(

                    ErrorCode.VALIDATOR_IMAGE_TOO_LARGE,
                    "ValidatorService::validateAndSanitize -> Profile picture too large",
                    userDetails.getProfilePicture().length(),
                    maxImageSize
                ));
            }
        }

        this.requireNull(userDetails.getCreatedAt(), "userDetails.createdAt");
        this.requireNull(userDetails.getUpdatedAt(), "userDetails.updatedAt");
        this.requireNull(userDetails.getCreatedBy(), "userDetails.createdBy");
        this.requireNull(userDetails.getUpdatedBy(), "userDetails.updatedBy");
        this.requireNullOrEmpty(userDetails.getSubscriptions(), "userDetails.subscriptions");
    }

    ///..
    /**
     * Validates the provided user login credentials.
     * @param credentials : The target login credentials.
     * @throws IllegalArgumentException If {@code credentials} doesn't pass validation.
    */
    public void validateCredentials(UsernamePasswordDto credentials) throws IllegalArgumentException {

        this.requireNotNull(credentials, "credentials");
        this.requireFilled(credentials.getUsername(), "credentials.username");
        this.requireFilled(credentials.getPassword(), "credentials.password");
    }

    ///..
    /**
     * Validates the provided user search filter.
     * @param searchFilter : The target search filter.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    public void validateSearchFilter(UserSearchFilterDto searchFilter) throws IllegalArgumentException {

        this.requireNotNull(searchFilter, "searchFilter");
        this.requireNotNull(searchFilter.getPageNumber(), "searchFilter.pageNumber");
        this.requireNotNull(searchFilter.getPageSize(), "searchFilter.pageSize");

        if(searchFilter.getUsernameLike() == null && searchFilter.getEmailLike() == null) {

            this.requireFilled(searchFilter.getRoles(), "searchFilter.roles");
            this.requireNotNull(searchFilter.getCreatedAtStart(), "searchFilter.createdAtStart");
            this.requireNotNull(searchFilter.getCreatedAtEnd(), "searchFilter.createdAtEnd");
            this.requireNullOrFilled(searchFilter.getCreatedByNames(), "searchFilter.createdBy");
            this.requireNotNull(searchFilter.getUpdatedAtStart(), "searchFilter.updatedAtStart");
            this.requireNotNull(searchFilter.getUpdatedAtEnd(), "searchFilter.updatedAtEnd");
            this.requireNullOrFilled(searchFilter.getUpdatedByNames(), "searchFilter.updatedBy");
            this.requireNullOrFilled(searchFilter.getSubscribedToNames(), "searchFilter.subscribedTo");
            this.requireNotNull(searchFilter.getFailedAccesses(), "searchFilter.failedAccesses");
        }
    }

    ///..
    /**
     * Validates the provided user subscription.
     * @param subscription : The target user subscription.
     * @throws IllegalArgumentException If {@code subscription} doesn't pass validation.
    */
    public void validateSubscription(SubscriptionDto subscription) throws IllegalArgumentException {

        this.requireNotNull(subscription, "subscription");
        this.requireNull(subscription.getId(), "subscription.id");
        this.requireNotNull(subscription.getPublisher(), "subscription.publisher");
        this.requireNull(subscription.getSubscriber(), "subscription.subscriber");
        this.requireNotNull(subscription.getNotify(), "subscription.notify");
        this.requireNull(subscription.getCreatedAt(), "subscription.createdAt");
        this.requireNull(subscription.getUpdatedAt(), "subscription.updatedAt");

        if(subscription.getPublisher().equals(subscription.getSubscriber())) {

            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_DIFFERENT, "", ""));
        }
    }

    ///..
    /**
     * Validates the provided user subscriptions.
     * @param subscriptions : The target user subscriptions.
     * @throws IllegalArgumentException If {@code subscriptions} doesn't pass validation.
    */
    public void validateSubscriptions(Iterable<SubscriptionDto> subscriptions) throws IllegalArgumentException {

        this.requireNotNull(subscriptions, "subscriptions");
        int i = 0;

        for(SubscriptionDto subscription : subscriptions) {

            this.requireNotNull(subscription, PREFIX + i + "].subscription");
            this.requireNotNull(subscription.getId(), PREFIX + i + "].id");
            this.requireNull(subscription.getPublisher(), PREFIX + i + "].publisher");
            this.requireNull(subscription.getSubscriber(), PREFIX + i + "].subscriber");
            this.requireNull(subscription.getCreatedAt(), PREFIX + i + "].createdAt");
            this.requireNull(subscription.getUpdatedAt(), PREFIX + i + "].updatedAt");

            i++;
        }
    }

    ///..
    /**
     * Validates the provided audit search filter.
     * @param auditSearchFilter : The target search filter.
     * @throws IllegalArgumentException If {@code auditSearchFilter} doesn't pass validation.
    */
    public void validateAuditSearchFilter(AuditSearchFilterDto auditSearchFilter) throws IllegalArgumentException {

        this.requireNotNull(auditSearchFilter, "auditSearchFilter");
        this.requireNotNull(auditSearchFilter.getPageNumber(), "auditSearchFilter.pageNumber");
        this.requireNotNull(auditSearchFilter.getPageSize(), "auditSearchFilter.pageSize");
        this.requireFilled(auditSearchFilter.getTableNames(), "auditSearchFilter.tableNames");
        this.requireFilled(auditSearchFilter.getAuditActions(), "auditSearchFilter.auditActions");
        this.requireNotNull(auditSearchFilter.getCreatedAtStart(), "auditSearchFilter.createdAtStart");
        this.requireNotNull(auditSearchFilter.getCreatedAtEnd(), "auditSearchFilter.createdAtEnd");
        this.requireNullOrFilled(auditSearchFilter.getCreatedByNames(), "auditSearchFilter.createdByNames");
    }

    ///..
    /**
     * Validates the provided log search filter.
     * @param logSearchFilter : The target search filter.
     * @throws IllegalArgumentException If {@code logSearchFilter} doesn't pass validation.
    */
    public void validateLogSearchFilter(LogSearchFilterDto logSearchFilter) throws IllegalArgumentException {

        this.requireNotNull(logSearchFilter, "logSearchFilter");
        this.requireNotNull(logSearchFilter.getPageNumber(), "logSearchFilter.pageNumber");
        this.requireNotNull(logSearchFilter.getPageSize(), "logSearchFilter.pageSize");
        this.requireNotNull(logSearchFilter.getTimestampStart(), "logSearchFilter.timestampStart");
        this.requireNotNull(logSearchFilter.getTimestampEnd(), "logSearchFilter.timestampEnd");
        this.requireFilled(logSearchFilter.getLevels(), "logSearchFilter.levels");
        this.requireFilled(logSearchFilter.getThreads(), "logSearchFilter.threads");
        this.requireNotNull(logSearchFilter.getMessageLike(), "logSearchFilter.message");
        this.requireNotNull(logSearchFilter.getCreatedAtStart(), "logSearchFilter.createdAtStart");
        this.requireNotNull(logSearchFilter.getCreatedAtEnd(), "logSearchFilter.createdAtEnd");
    }

    ///..
    /**
     * Validates the provided username-email dto.
     * @param usernameEmail : The target username-email dto.
     * @throws IllegalArgumentException If {@code usernameEmail} doesn't pass validation.
    */
    public void validateUsernameEmail(UsernameEmailDto usernameEmail) throws IllegalArgumentException {

        this.requireNotNull(usernameEmail, "usernameEmail");
        this.requireFilled(usernameEmail.getUsername(), "usernameEmail.username");
        this.requireFilled(usernameEmail.getEmail(), "usernameEmail.email");
        this.validateEmail(usernameEmail.getEmail(), "usernameEmail.email");
    }

    ///..
    /**
     * Validates the provided forgot password dto.
     * @param forgotPassword : The target forgot password.
     * @throws IllegalArgumentException If {@code forgotPassword} doesn't pass validation.
    */
    public void validateForgotPasswordDto(ForgotPasswordDto forgotPassword) throws IllegalArgumentException {

        this.requireNotNull(forgotPassword, "forgotPassword");
        this.requireFilled(forgotPassword.getForgotPasswordSessionId(), "forgotPassword.forgotPasswordSessionId");
        this.validatePassword(forgotPassword.getPassword(), "forgotPassword.password");
    }

    ///..
    /**
     * Validates the provided authorization request broker event.
     * @param authRequest : The event to validate.
     * @throws IllegalArgumentException If {@code authRequest} doesn't pass validation.
    */
    public void validateAuthRequestEvent(AuthRequest authRequest) throws IllegalArgumentException {

        this.requireNotNull(authRequest, "authRequest");
        this.requireNotNull(authRequest.getSessionId(), "authRequest.sessionId");
        this.requireNotNull(authRequest.getRequiredRoles(), "authRequest.requiredRoles");
    }

    ///
}
