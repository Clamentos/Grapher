package io.github.clamentos.grapher.auth.messaging;

///
import io.github.clamentos.grapher.auth.utility.Permission;
import io.github.clamentos.grapher.auth.utility.UserSession;

///.
import java.util.List;
import java.util.Map;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public final class ConsumeExchangeMessage {

    ///
    private final String sessionId;
    private final UserSession session;
    private final Map<String, List<Permission>> apiPermissions;

    ///
}
