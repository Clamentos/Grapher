package io.github.clamentos.grapher.auth.web.controllers;

import io.github.clamentos.grapher.auth.business.services.UserService;
///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UserSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.List;

///..
import java.util.concurrent.CompletionException;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.dao.DataAccessException;

///..
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
@RestController
@RequestMapping(path = "/grapher/v1/user") // TODO: revise exceptions

///
public final class UserController {

    ///
    private final UserService userService;

    ///
    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    ///
    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<Void> register(@RequestAttribute(name = "session") Session session, @RequestBody UserDto userDetails)
    throws DataAccessException, EntityExistsException, IllegalArgumentException {

        userService.register(session, userDetails);
        return(ResponseEntity.ok().build());
    }

    ///..
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthDto> login(@RequestBody UsernamePassword credentials)
    throws AuthenticationException, AuthorizationException, DataAccessException, EntityNotFoundException, IllegalArgumentException {

        return(ResponseEntity.ok(userService.login(credentials)));
    }

    ///..
    @DeleteMapping(path = "/logout")
    public ResponseEntity<Void> logout(@RequestAttribute(name = "session") Session session)
    throws DataAccessException, NullPointerException {

        userService.logout(session);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping(path = "/logout/all")
    public ResponseEntity<Void> logoutAll(@RequestAttribute(name = "session") Session session) throws NullPointerException {

        userService.logoutAll(session);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(path = "/search", produces = "application/json")
    public ResponseEntity<List<UserDto>> getUsers(

        @RequestAttribute(name = "session") Session session,
        @RequestBody UserSearchFilterDto searchFilter

    ) throws DataAccessException, IllegalArgumentException, NullPointerException {

        return(ResponseEntity.ok(userService.getAllUsers(session, searchFilter)));
    }

    ///..
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUser(

        @RequestAttribute(name = "session") Session session,
        @PathVariable(name = "id") long id

    ) throws DataAccessException, EntityNotFoundException {

        return(ResponseEntity.ok(userService.getUserById(session, id)));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updateUser(@RequestAttribute(name = "session") Session session, @RequestBody UserDto userDetails)
    throws CompletionException, DataAccessException, EntityNotFoundException, IllegalArgumentException, NullPointerException {

        userService.updateUser(session, userDetails);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@RequestAttribute(name = "session") Session session, @PathVariable(name = "id") long id)
    throws AuthorizationException, DataAccessException, NullPointerException {

        userService.deleteUser(session, id);
        return(ResponseEntity.ok().build());
    }

    ///
}
