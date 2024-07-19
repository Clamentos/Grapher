package io.github.clamentos.grapher.auth.utility;

///
import java.util.Set;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public final class UserSession {

    ///
    private final long userId;
    private final String username;
    private final Set<Short> operationIds;
    private final long expiresAt;

    ///
}
