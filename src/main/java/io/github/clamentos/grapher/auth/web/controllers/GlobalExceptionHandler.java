package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.utility.Constants;

///..
import io.github.clamentos.grapher.auth.web.dtos.ErrorDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import org.springframework.http.ResponseEntity;

///.
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

///..
import org.springframework.web.context.request.WebRequest;

///..
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

///
@ControllerAdvice

///
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    ///
    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException exc, WebRequest request) {

        return(constructError(exc, request, 401, "Requester could not be authenticated"));
    }

    ///..
    @ExceptionHandler(value = AuthorizationException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationException(AuthorizationException exc, WebRequest request) {

        return(constructError(exc, request, 403, "Requester could not be authorized"));
    }

    ///..
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException exc, WebRequest request) {

        return(constructError(exc, request, 404, "Entity could not found"));
    }

    ///..
    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity<ErrorDto> handleSecurityException(SecurityException exc, WebRequest request) {

        return(constructError(exc, request, 401, "Unauthenticated"));
    }

    ///..
    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<ErrorDto> handleEntityExistsException(EntityExistsException exc, WebRequest request) {

        return(constructError(exc, request, 409, "Entity already exists"));
    }

    ///..
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exc, WebRequest request) {

        return(constructError(exc, request, 400, "Bad data format or values"));
    }

    ///.
    private ResponseEntity<ErrorDto> constructError(Exception exc, WebRequest request, int status, String message) {

        String[] splits;

        String errorCode = "EC999";
        String[] arguments = new String[0];
        long now = System.currentTimeMillis();

        if(exc.getMessage() != null) {

            splits = exc.getMessage().split(Constants.ERROR_CODE_SEPARATOR);

            if(splits.length > 0) {

                if(ErrorCode.isValidErrorCode(splits[0])) {

                    errorCode = splits[0];

                    if(splits.length > 1) {

                        arguments = splits[1].split(Constants.ERROR_ARG_SEPARATOR);
                    }
                }
            }
        }

        return(ResponseEntity.status(status).body(new ErrorDto(request.getContextPath(), now, errorCode, message, arguments)));
    }

    ///
}
