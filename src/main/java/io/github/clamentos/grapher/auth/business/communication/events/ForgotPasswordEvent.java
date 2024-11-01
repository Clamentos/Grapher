package io.github.clamentos.grapher.auth.business.communication.events;

///
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Forgot Password Event</h3>
 * Outgoing-only DTO used for triggering a "forgot password" notification.
*/

///
@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
@Getter

///
public final class ForgotPasswordEvent {

    ///
    private final String forgotPasswordSessionId;
    private final long expiresAt;

    ///..
    private final String username;
    private final String email;

    ///
}
