package io.github.clamentos.grapher.auth.business.communication.events;

///
import com.fasterxml.jackson.annotation.JsonIgnore;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Forgot Password Event</h3>
 * Outgoing-only DTO used for triggering a "forgot password" notification.
*/

///
@AllArgsConstructor
@Getter

///
public final class ForgotPasswordEvent {

    ///
    @JsonIgnore
    public static final String TYPE = "ForgotPasswordEvent";

    ///.
    private final String forgotPasswordSessionId;
    private final String username;
    private final String email;
    private final long expiresAt;

    ///
}
