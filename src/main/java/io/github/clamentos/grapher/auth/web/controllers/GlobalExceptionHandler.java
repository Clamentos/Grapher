package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;
import io.github.clamentos.grapher.auth.error.exceptions.NotificationException;
import io.github.clamentos.grapher.auth.error.exceptions.ServiceUnavailableException;

///..
import io.github.clamentos.grapher.auth.web.dtos.ErrorDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.time.format.DateTimeParseException;

///..
import java.util.ArrayList;
import java.util.List;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.dao.DataAccessException;

///..
import org.springframework.http.ResponseEntity;

///..
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
     * Internal Spring {@link ExceptionHandler} for {@link AuthenticationException}. Responds with a {@code 401}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = AuthenticationException.class)
    protected ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException exc, WebRequest request) {

        return(constructError(exc, request, 401));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link AuthorizationException}. Responds with a {@code 403}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = AuthorizationException.class)
    protected ResponseEntity<ErrorDto> handleAuthorizationException(AuthorizationException exc, WebRequest request) {

        return(constructError(exc, request, 403));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link DataAccessException}. Responds with a {@code 422}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = DataAccessException.class)
    protected ResponseEntity<ErrorDto> handleDataAccessException(DataAccessException exc, WebRequest request) {

        log.error("{}: {}", exc.getClass().getSimpleName(), exc.getMessage());
        return(constructError(exc, request, 422));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link DateTimeParseException}. Responds with a {@code 400}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = DateTimeParseException.class)
    protected ResponseEntity<ErrorDto> handleDateTimeParseException(DateTimeParseException exc, WebRequest request) {

        return(constructError(exc, request, 400));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link EntityNotFoundException}. Responds with a {@code 404}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException exc, WebRequest request) {

        return(constructError(exc, request, 404));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link EntityExistsException}. Responds with a {@code 409}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = EntityExistsException.class)
    protected ResponseEntity<ErrorDto> handleEntityExistsException(EntityExistsException exc, WebRequest request) {

        return(constructError(exc, request, 409));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link IllegalArgumentException}. Responds with a {@code 400}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exc, WebRequest request) {

        return(constructError(exc, request, 400));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link NotificationException}. Responds with a {@code 422}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = NotificationException.class)
    protected ResponseEntity<ErrorDto> handleNotificationException(NotificationException exc, WebRequest request) {

        return(constructError(exc, request, 422));
    }

    ///..
    /**
     * Internal Spring {@link ExceptionHandler} for {@link ServiceUnavailableException}. Responds with a {@code 503}.
     * @param exc : The target exception to handle.
     * @param request : The associated web request.
     * @return The never {@code null} HTTP response with the error details.
    */
    @ExceptionHandler(value = ServiceUnavailableException.class)
    protected ResponseEntity<ErrorDto> handleUnavailableException(ServiceUnavailableException exc, WebRequest request) {

        return(constructError(exc, request, 503));
    }

    ///.
    private ResponseEntity<ErrorDto> constructError(Throwable exc, WebRequest request, int status) {

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

                ((ServletWebRequest)request).getRequest().getRequestURI(),
                System.currentTimeMillis(),
                errorCode,
                arguments
            )
        ));
    }

    ///
}
