package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private final String[] parameters;

    ///
}
