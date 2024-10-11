package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.web.dtos.ErrorDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.ArrayList;
import java.util.List;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.dao.DataAccessException;

///..
import org.springframework.http.ResponseEntity;

///.
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

///..
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

///..
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

///
/**
 * <h3>Global Exception Handler</h3>
 * Spring {@link ControllerAdvice} to handle various kinds of {@link RuntimeException} and return the appropriate HTTP response.
*/

///
@ControllerAdvice
@Slf4j

///
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    ///
    /**
     * Handle {@link AuthenticationException} and respond with a {@code 401}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException exc, WebRequest request) {

        return(constructError(exc, request, 401));
    }

    ///..
    /**
     * Handle {@link AuthorizationException} and respond with a {@code 403}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = AuthorizationException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationException(AuthorizationException exc, WebRequest request) {

        return(constructError(exc, request, 403));
    }

    ///..
    /**
     * Handle {@link DataAccessException} and respond with a {@code 422}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = DataAccessException.class)
    public ResponseEntity<ErrorDto> handleDataAccessException(DataAccessException exc, WebRequest request) {

        log.error("{}: {}", exc.getClass().getSimpleName(), exc.getMessage());
        return(constructError(exc, request, 422));
    }

    ///..
    /**
     * Handle {@link EntityNotFoundException} and respond with a {@code 404}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException exc, WebRequest request) {

        return(constructError(exc, request, 404));
    }

    ///..
    /**
     * Handle {@link EntityExistsException} and respond with a {@code 409}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<ErrorDto> handleEntityExistsException(EntityExistsException exc, WebRequest request) {

        return(constructError(exc, request, 409));
    }

    ///..
    /**
     * Handle {@link IllegalArgumentException} and respond with a {@code 400}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exc, WebRequest request) {

        return(constructError(exc, request, 400));
    }

    ///.
    private ResponseEntity<ErrorDto> constructError(RuntimeException exc, WebRequest request, int status) {

        String message = exc.getMessage() != null ? exc.getMessage() : "";
        String[] splits = message.split("/");

        ErrorCode errorCode = ErrorCode.getDefault();
        List<String> arguments = new ArrayList<>();

        if(splits.length >= 1) errorCode = ErrorCode.valueOf(splits[0]);

        if(splits.length >= 3) {

            for(int i = 2; i < splits.length; i++) arguments.add(splits[i]);
        }

        return(ResponseEntity.status(status).body(

            new ErrorDto(

                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                System.currentTimeMillis(),
                errorCode,
                arguments
            )
        ));
    }

    ///
}
