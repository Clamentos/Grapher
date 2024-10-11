package io.github.clamentos.grapher.auth.web.dtos;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;

///.
import java.util.List;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Error Dto</h3>
 * Outgoing-only DTO for HTTP error responses.
*/

///
@AllArgsConstructor
@Getter

///
public final class ErrorDto {

    ///
    private final String url;
    private final long timestamp;
    private final ErrorCode errorCode;
    private final List<String> messageArguments;

    ///
}
