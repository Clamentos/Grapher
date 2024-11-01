package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.UserService;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;
import io.github.clamentos.grapher.auth.error.exceptions.IllegalActionException;
import io.github.clamentos.grapher.auth.error.exceptions.NotificationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.ForgotPasswordDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UserSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernameEmailDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePasswordDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

///..
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

///
/**
 * <h3>User Controller</h3>
 * Spring {@link RestController} that exposes user account management APIs.
*/

///
@RestController
@RequestMapping(path = "/grapher/v1/auth-service/user")

///
public final class UserController {

    ///
    private final UserService userService;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }

    ///
    /**
     * Registers a new user with the specified details.
     * @param session : The session of the calling user, if available.
     * @param userDetails : The user details.
     * @throws DataAccessException If any database access errors occur.
     * @throws EntityExistsException If the specified user already exists.
     * @throws IllegalArgumentException If {@code userDetails} fails validation.
    */
    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<Void> register(

        @RequestAttribute(name = "session", required = false) Session session,
        @RequestBody UserDto userDetails

    ) throws DataAccessException, EntityExistsException, IllegalArgumentException {

        userService.register(session, userDetails);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Logs the calling user in.
     * @param credentials : The user's credentials.
     * @return The never {@code null} login details.
     * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code credentials} doesn't pass validation.
    */
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthDto> login(@RequestBody UsernamePasswordDto credentials)
    throws AuthenticationException, AuthorizationException, DataAccessException, IllegalArgumentException {

        AuthDto authDto = userService.login(credentials);
        HttpHeaders headers = new HttpHeaders();

        headers.add(

            "Set-Cookie",
            "sessionIdCookie=" + authDto.getSessionId() + "; Max-Age=" + (authDto.getSessionExpiresAt() / 1000) +
            "; Path=/grapher/v1; Secure; HttpOnly"
        ); // add domain

        return(new ResponseEntity<>(authDto, headers, HttpStatus.OK));
    }

    ///..
    /**
     * Logs the calling user out of the specified session.
     * @param session : The session of the calling user.
     * @throws AuthenticationException If authentication fails.
     * @throws DataAccessException If any database access error occurs.
    */
    @DeleteMapping(path = "/logout")
    public ResponseEntity<Void> logout(@RequestAttribute(name = "session") Session session)
    throws AuthenticationException, DataAccessException {

        userService.logout(session);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Logs the calling user out of all sessions.
     * @param session : The session of the calling user.
    */
    @DeleteMapping(path = "/logout/all")
    public ResponseEntity<Void> logoutAll(@RequestAttribute(name = "session") Session session) {

        userService.logoutAll(session);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Gets all the users that match the provided search filter.
     * @param session : The session of the calling user.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of minimal user details.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    @GetMapping(path = "/search", produces = "application/json")
    public ResponseEntity<List<UserDto>> getAllUsersByFilter(

        @RequestAttribute(name = "session") Session session,
        @RequestBody UserSearchFilterDto searchFilter

    ) throws DataAccessException, IllegalArgumentException {

        return(ResponseEntity.ok(userService.getAllUsersByFilter(session, searchFilter)));
    }

    ///..
    /**
     * Gets the details of the specified user.
     * @param session : The session of the calling user.
     * @param id : The id of the target user.
     * @return The never {@code null} user details.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If {@code id} doesn't match to any user.
    */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUserById(@RequestAttribute(name = "session") Session session, @PathVariable(name = "id") long id)
    throws DataAccessException, EntityNotFoundException {

        return(ResponseEntity.ok(userService.getUserById(session, id)));
    }

    ///..
    /**
     * Updates the specified user with the provided parameters.
     * @param session : The session of the calling user.
     * @param userDetails : The new user details to be merged with the existing ones.
     * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If the target user doesn't exist.
     * @throws IllegalActionException If the calling user attempts to perform an illegal action.
     * @throws IllegalArgumentException If {@code userDetails} doesn't pass validation.
    */
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updateUser(@RequestAttribute(name = "session") Session session, @RequestBody UserDto userDetails)
    throws AuthenticationException, AuthorizationException, DataAccessException, EntityNotFoundException, IllegalActionException,
    IllegalArgumentException {

        userService.updateUser(session, userDetails);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Deletes the specified user and removes all of its sessions.
     * @param session : The session of the calling user.
     * @param id : The id of the target user.
     * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If the target user is not found.
    */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@RequestAttribute(name = "session") Session session, @PathVariable(name = "id") long id)
    throws AuthorizationException, DataAccessException {

        userService.deleteUser(session, id);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Starts a forgot password session.
     * @param usernameEmail : The user details.
     * @throws AuthorizationException If authorization fails.
     * @throws IllegalArgumentException If {@code usernameEmail} doesn't pass validation.
     * @throws NotificationException If the notification cannot be sent to the message broker.
    */
    @GetMapping(path = "/forgot-password", consumes = "application/json")
    public ResponseEntity<Void> startForgotPassword(@RequestBody UsernameEmailDto usernameEmail)
    throws AuthorizationException, IllegalArgumentException, NotificationException {

        userService.startForgotPassword(usernameEmail);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Ends a forgot password session and applies the changes.
     * @param forgotPassword : The forgot password details.
     * @throws AuthorizationException If authorization fails.
     * @throws DataAccessException If any database access error occurs.
     * @throws EntityNotFoundException If the target user is not found.
     * @throws IllegalArgumentException If {@code forgotPassword} doesn't pass validation.
    */
    @PostMapping(path = "/forgot-password", consumes = "application/json")
    public ResponseEntity<Void> endForgotPassword(@RequestBody ForgotPasswordDto forgotPassword)
    throws AuthorizationException, DataAccessException, EntityNotFoundException, IllegalArgumentException {

        userService.endForgotPassword(forgotPassword);
        return(ResponseEntity.ok().build());
    }

    ///
}
