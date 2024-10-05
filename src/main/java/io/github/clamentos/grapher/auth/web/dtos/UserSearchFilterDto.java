package io.github.clamentos.grapher.auth.web.dtos;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.List;

///.
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>User Search Filter Dto</h3>
 * Ingoing-only DTO for user search HTTP requests.
*/

///
@Getter
@Setter

///
public final class UserSearchFilterDto {

    ///
    private Integer pageNumber;
    private Integer pageSize;

    ///..
    private String usernamePattern;
    private String emailPattern;
    private List<UserRole> roles;
    private Long createdAtStart;
    private Long createdAtEnd;
    private List<String> createdBy;
    private Long updatedAtStart;
    private Long updatedAtEnd;
    private List<String> updatedBy;
    private List<String> subscribedTo;
    private Boolean lockedCheckMode;
    private Boolean expiredPasswordCheckMode;
    private Short failedAccesses;

    ///
}
