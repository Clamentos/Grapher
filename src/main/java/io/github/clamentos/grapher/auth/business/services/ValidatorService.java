package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UserSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

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
    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;
    private final int maxImageSize;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    public ValidatorService() {

        usernamePattern = Pattern.compile("^[a-zA-Z0-9-_.]{3,32}$");
        emailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=?!])(?=\\S+$).{10,32}$");
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

        if(coll != null && coll.size() != 0)
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

        if(coll != null && coll.size() == 0)
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

        if(coll == null || coll.size() == 0)
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

        if(coll == null || coll.size() == 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));

        for(Object elem : coll) this.requireNotNull(elem, elemName);
    }

    ///..
    /**
     * Validates a username with the following regex: ^[a-zA-Z0-9._-]{3,32}$.
     * @param username : The target username.
     * @param name : The name of the field.
     * @throws IllegalArgumentException If {@code username} doesn't pass validation.
    */
    public void validateUsername(String username, String name) throws IllegalArgumentException {

        if(username == null || username.length() == 0 || usernamePattern.matcher(username).matches() == false)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_BAD_USERNAME, "", name));
    }

    ///..
    /**
     * Validates a password with the following regex: ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{10,32}$.
     * @param password : The target password.
     * @param name : The name of the field.
     * @throws IllegalArgumentException If {@code password} doesn't pass validation.
    */
    public void validatePassword(String password, String name) throws IllegalArgumentException {

        if(password == null || password.length() == 0 || passwordPattern.matcher(password).matches() == false)
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

        if(email != null && email.length() > 0 && emailPattern.matcher(email).matches() == false)
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

        this.requireNotNull(userDetails, "body");

        if(isUpdate) {

            this.requireNotNull(userDetails.getId(), "id");
            this.requireNull(userDetails.getUsername(), "username");
            this.requireNull(userDetails.getFailedAccesses(), "failedAccesses");
            this.requireNull(userDetails.getPasswordLastChangedAt(), "passwordLastChangedAt");
        }

        else {

            this.requireNull(userDetails.getId(), "id");
            this.validateUsername(userDetails.getUsername(), "username");
            this.validatePassword(userDetails.getPassword(), "password");
            this.validateEmail(userDetails.getEmail(), "email");
            this.requireNull(userDetails.getRole(), "role");
            this.requireNull(userDetails.getFailedAccesses(), "failedAccesses");
            this.requireNull(userDetails.getLockedUntil(), "lockedUntil");
            this.requireNull(userDetails.getPasswordLastChangedAt(), "passwordLastChangedAt");

            if(userDetails.getEmail() == null) userDetails.setEmail("");
            if(userDetails.getAbout() == null) userDetails.setAbout("");

            if(userDetails.getProfilePicture() != null && userDetails.getProfilePicture().length() > maxImageSize) {

                throw new IllegalArgumentException(ErrorFactory.create(

                    ErrorCode.VALIDATOR_IMAGE_TOO_LARGE,
                    "ValidatorService::validateAndSanitize -> Profile picture too large",
                    userDetails.getProfilePicture().length(),
                    maxImageSize
                ));
            }
        }

        this.requireNull(userDetails.getCreatedAt(), "createdAt");
        this.requireNull(userDetails.getUpdatedAt(), "updatedAt");
        this.requireNull(userDetails.getCreatedBy(), "createdBy");
        this.requireNull(userDetails.getUpdatedBy(), "updatedBy");
        this.requireNullOrEmpty(userDetails.getSubscriptions(), "subscriptions");
    }

    ///..
    /**
     * Validates the provided user login credentials.
     * @param credentials : The target login credentials.
     * @throws IllegalArgumentException If {@code credentials} doesn't pass validation.
    */
    public void validateCredentials(UsernamePassword credentials) throws IllegalArgumentException {

        this.requireNotNull(credentials, "body");
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

        this.requireNotNull(searchFilter, "body");
        this.requireNotNull(searchFilter.getPageNumber(), "pageNumber");
        this.requireNotNull(searchFilter.getPageSize(), "pageSize");

        if(searchFilter.getUsernameLike() == null && searchFilter.getEmailLike() == null) {

            this.requireFilled(searchFilter.getRoles(), "roles");
            this.requireNotNull(searchFilter.getCreatedAtStart(), "createdAtStart");
            this.requireNotNull(searchFilter.getCreatedAtEnd(), "createdAtEnd");
            this.requireNullOrFilled(searchFilter.getCreatedByNames(), "createdBy");
            this.requireNotNull(searchFilter.getUpdatedAtStart(), "updatedAtStart");
            this.requireNotNull(searchFilter.getUpdatedAtEnd(), "updatedAtEnd");
            this.requireNullOrFilled(searchFilter.getUpdatedByNames(), "updatedBy");
            this.requireNullOrFilled(searchFilter.getSubscribedToNames(), "subscribedTo");
            this.requireNotNull(searchFilter.getFailedAccesses(), "failedAccesses");
        }
    }

    ///..
    /**
     * Validates the provided user subscription.
     * @param subscription : The target user subscription.
     * @throws IllegalArgumentException If {@code subscription} doesn't pass validation.
    */
    public void validateSubscription(SubscriptionDto subscription) throws IllegalArgumentException {

        this.requireNotNull(subscription, "body");
        this.requireNull(subscription.getId(), "id");
        this.requireNotNull(subscription.getPublisher(), "publisher");
        this.requireNotNull(subscription.getNotify(), "notify");
        this.requireNull(subscription.getCreatedAt(), "createdAt");
        this.requireNull(subscription.getUpdatedAt(), "updatedAt");
    }

    ///..
    /**
     * Validates the provided user subscriptions.
     * @param subscriptions : The target user subscriptions.
     * @throws IllegalArgumentException If {@code subscriptions} doesn't pass validation.
    */
    public void validateSubscriptions(Iterable<SubscriptionDto> subscriptions) throws IllegalArgumentException {

        this.requireNotNull(subscriptions, "body");

        for(SubscriptionDto subscription : subscriptions) {

            this.requireNotNull(subscription, "subscription");
            this.requireNotNull(subscription.getId(), "id");
            this.requireNull(subscription.getPublisher(), "publisher");
            this.requireNull(subscription.getCreatedAt(), "createdAt");
            this.requireNull(subscription.getUpdatedAt(), "updatedAt");
        }
    }

    ///..
    /**
     * Validates the provided audit search filter.
     * @param searchFilter : The target search filter.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    public void validateAuditSearchFilter(AuditSearchFilter searchFilter) throws IllegalArgumentException {

        this.requireNotNull(searchFilter, "body");
        this.requireNotNull(searchFilter.getPageNumber(), "pageNumber");
        this.requireNotNull(searchFilter.getPageSize(), "pageSize");
        this.requireFilled(searchFilter.getTableNames(), "tableNames");
        this.requireFilled(searchFilter.getAuditActions(), "auditActions");
        this.requireNotNull(searchFilter.getCreatedAtStart(), "createdAtStart");
        this.requireNotNull(searchFilter.getCreatedAtEnd(), "createdAtEnd");
        this.requireNullOrFilled(searchFilter.getCreatedByNames(), "createdByNames");
    }

    ///..
    /**
     * Validates the provided log search filter.
     * @param searchFilter : The target search filter.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    public void validateLogSearchFilter(LogSearchFilter searchFilter) throws IllegalArgumentException {

        this.requireNotNull(searchFilter, "body");
        this.requireNotNull(searchFilter.getPageNumber(), "pageNumber");
        this.requireNotNull(searchFilter.getPageSize(), "pageSize");
        this.requireNotNull(searchFilter.getTimestampStart(), "timestampStart");
        this.requireNotNull(searchFilter.getTimestampEnd(), "timestampEnd");
        this.requireFilled(searchFilter.getLevels(), "levels");
        this.requireFilled(searchFilter.getThreads(), "threads");
        this.requireNotNull(searchFilter.getMessage(), "message");
        this.requireNotNull(searchFilter.getCreatedAtStart(), "createdAtStart");
        this.requireNotNull(searchFilter.getCreatedAtEnd(), "createdAtEnd");
    }

    ///
}
