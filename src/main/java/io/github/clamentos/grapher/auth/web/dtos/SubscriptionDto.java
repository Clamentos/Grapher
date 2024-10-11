package io.github.clamentos.grapher.auth.web.dtos;

///
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
/**
 * <h3>Subscription Dto</h3>
 * Bidirectional DTO for user subscription related HTTP requests and responses.
*/

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)

///
public final class SubscriptionDto {

    ///
    private Long id;
    private Long publisher;
    private Boolean notify;
    private Long createdAt;
    private Long updatedAt;

    ///
}
