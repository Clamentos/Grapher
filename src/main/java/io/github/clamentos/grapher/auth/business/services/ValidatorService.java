package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilter;
///..
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
@Service

///
public class ValidatorService { //TODO: patterns & jdoc & finish 2 validators

    ///
    private final Pattern aboutPattern;
    private final Pattern usernamePattern;
    private final Pattern emailPattern;
    private final Pattern passwordPattern;
    private final int maxImageSize;

    ///
    public ValidatorService() {

        aboutPattern = Pattern.compile("...");
        usernamePattern = Pattern.compile("...");
        emailPattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{10,50}$");
        maxImageSize = 275252; // ceil(256*256*3*1.4)
    }

    ///
    public void requireNull(Object obj, String name) throws IllegalArgumentException {

        if(obj != null) throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NULL, "", name));
    }

    ///..
    public void requireNullOrEmpty(Collection<?> coll, String name) throws IllegalArgumentException {

        if(coll != null && coll.size() != 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NULL_OR_EMPTY, "", name));
    }

    ///..
    public void requireNullOrFilled(Collection<?> coll, String name) throws IllegalArgumentException {

        if(coll != null && coll.size() == 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NULL_OR_FILLED, "", name));
    }

    ///..
    public void requireNotNull(Object obj, String name) throws IllegalArgumentException {

        if(obj == null) throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_NOT_NULL, "", name));
    }

    ///..
    public void requireFilled(String str, String name) throws IllegalArgumentException {

        if(str == null || str.length() == 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));
    }

    ///..
    public void requireFilled(Collection<?> coll, String name) throws IllegalArgumentException {

        if(coll == null || coll.size() == 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));
    }

    ///..
    public void requireFullyFilled(Collection<?> coll, String name, String elemName) throws IllegalArgumentException {

        if(coll == null || coll.size() == 0)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_REQUIRE_FILLED, "", name));

        for(Object elem : coll) this.requireNotNull(elem, elemName);
    }

    ///..
    public void validateUsername(String username, String name) throws IllegalArgumentException {

        if(username == null || username.length() == 0 || usernamePattern.matcher(username).matches() == false)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_BAD_USERNAME, "", name));
    }

    ///..
    public void validatePassword(String password, String name) throws IllegalArgumentException {

        if(password == null || password.length() == 0 || passwordPattern.matcher(password).matches() == false)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_PASSWORD_TOO_WEAK, "", name));
    }

    ///..
    public void validateEmail(String email, String name) throws IllegalArgumentException {

        if(email != null && email.length() > 0 && emailPattern.matcher(email).matches() == false)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.VALIDATOR_BAD_EMAIL, "", name));
    }

    ///..
    public void validateAbout(String about, String name) throws IllegalArgumentException {

        if(about != null && about.length() > 0 || aboutPattern.matcher(about).matches() == false)
            throw new IllegalArgumentException(ErrorFactory.create(ErrorCode.BAD_FORMAT, "", name));
    }

    ///..
    public void validateAndSanitize(UserDto userDetails, boolean isUpdate) throws IllegalArgumentException {

        this.requireNotNull(userDetails, "body");

        if(isUpdate) {

            this.requireNull(userDetails.getId(), "id");
            this.validateUsername(userDetails.getUsername(), "username");
            this.validatePassword(userDetails.getPassword(), "password");
            this.validateEmail(userDetails.getEmail(), "email");
            this.validateAbout(userDetails.getAbout(), "about");
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

        else {

            this.requireNotNull(userDetails.getId(), "id");
            this.requireNull(userDetails.getUsername(), "username");
            this.requireNull(userDetails.getFailedAccesses(), "failedAccesses");
            this.requireNull(userDetails.getPasswordLastChangedAt(), "passwordLastChangedAt");
        }

        this.requireNull(userDetails.getCreatedAt(), "createdAt");
        this.requireNull(userDetails.getUpdatedAt(), "updatedAt");
        this.requireNull(userDetails.getCreatedBy(), "createdBy");
        this.requireNull(userDetails.getUpdatedBy(), "updatedBy");
        this.requireNullOrEmpty(userDetails.getSubscriptions(), "subscriptions");
    }

    ///..
    public void validateCredentials(UsernamePassword credentials) throws IllegalArgumentException {

        this.requireNotNull(credentials, "body");
        this.requireFilled(credentials.getUsername(), "credentials.username");
        this.requireFilled(credentials.getPassword(), "credentials.password");
    }

    ///..
    public void validateSearchFilter(UserSearchFilterDto filter) throws IllegalArgumentException {

        this.requireNotNull(filter, "body");
        this.requireNotNull(filter.getPageNumber(), "pageNumber");
        this.requireNotNull(filter.getPageSize(), "pageSize");

        if(filter.getUsernamePattern() == null && filter.getEmailPattern() == null) {

            this.requireFilled(filter.getRoles(), "roles");
            this.requireNotNull(filter.getCreatedAtStart(), "createdAtStart");
            this.requireNotNull(filter.getCreatedAtEnd(), "createdAtEnd");
            this.requireNullOrFilled(filter.getCreatedBy(), "createdBy");
            this.requireNotNull(filter.getUpdatedAtStart(), "updatedAtStart");
            this.requireNotNull(filter.getUpdatedAtEnd(), "updatedAtEnd");
            this.requireNullOrFilled(filter.getUpdatedBy(), "updatedBy");
            this.requireNullOrFilled(filter.getSubscribedTo(), "subscribedTo");
            this.requireNotNull(filter.getFailedAccesses(), "failedAccesses");
        }
    }

    ///..
    public void validateSubscription(SubscriptionDto subscription) throws IllegalArgumentException {

        this.requireNotNull(subscription, "body");
        this.requireNull(subscription.getId(), "id");
        this.requireNotNull(subscription.getPublisher(), "publisher");
        this.requireNotNull(subscription.getNotify(), "notify");
        this.requireNull(subscription.getCreatedAt(), "createdAt");
        this.requireNull(subscription.getUpdatedAt(), "updatedAt");
    }

    ///..
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
    public void validateAuditSearchFilter(AuditSearchFilter searchFilter) throws IllegalArgumentException {

        //...
    }

    ///..
    public void validateLogSearchFilter(LogSearchFilter searchFilter) throws IllegalArgumentException {

        // ...
    }

    ///
}
