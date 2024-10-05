package io.github.clamentos.grapher.auth.web.dtos;

///
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
    private final String errorCode;
    private final String message;
    private final List<String> parameters;

    ///
}
