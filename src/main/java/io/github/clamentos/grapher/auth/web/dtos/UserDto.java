package io.github.clamentos.grapher.auth.web.dtos;

///
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.List;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
/**
 * <h3>User Dto</h3>
 * Bidirectional DTO for user related HTTP requests and responses.
*/

///
@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

///
public final class UserDto {

    ///
    private Long id;
    private String username;
    private String password;
    private String email;
    private String profilePicture;
    private String about;
    private String preferences;
    private UserRole role;
    private Short failedAccesses;
    private Long lockedUntil;
    private String lockReason;
    private Long passwordLastChangedAt;
    private Long createdAt;
    private String createdBy;
    private Long updatedAt;
    private String updatedBy;

    ///..
    private Long subscriberCount;
    private List<SubscriptionDto> subscriptions;

    ///
}
