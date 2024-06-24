package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.TokenService;
import io.github.clamentos.grapher.auth.business.services.UserService;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.utility.TokenUtils;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

///.
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.http.ResponseEntity;

///..
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(path = "/v1/grapher/user")

///
public final class UserController {

    ///
    private final UserService service;
    private final TokenService tokenService;

    ///
    @Autowired
    public UserController(UserService service, TokenService tokenService) {

        this.service = service;
        this.tokenService = tokenService;
    }

    ///
    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<Void> register(@RequestBody UserDto user) {

        service.register(user);
        return(ResponseEntity.ok().build());
    }

    ///..
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthDto> login(@RequestBody UsernamePassword credentials) {

        return(ResponseEntity.ok(service.login(credentials)));
    }

    ///..
    @DeleteMapping(path = "/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization") String token) {

        tokenService.authenticate(token);
        tokenService.blacklistToken(token);

        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UserDto>> getUsers(

        @RequestHeader(name = "Authorization") String token,
        @RequestParam(name = "username", required = false, defaultValue = "") String username,
        @RequestParam(name = "email", required = false, defaultValue = "") String email,
        @RequestParam(name = "createdAtRange", required = false, defaultValue = "") String createdAtRange,
        @RequestParam(name = "updatedAtRange", required = false, defaultValue = "") String updatedAtRange,
        @RequestParam(name = "operations", required = false, defaultValue = "") String operations
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "GET/v1/grapher/user");

        return(ResponseEntity.ok(service.getAllUsers(username, email, createdAtRange, updatedAtRange, operations)));
    }

    ///..
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUser(@RequestHeader(name = "Authorization") String token, @PathVariable(name = "id") short id) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "GET/v1/grapher/user/{id}");

        return(ResponseEntity.ok(service.getUserById(id)));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updateUser(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody UserDto user
    ) {

        tokenService.authenticate(token);

        boolean canModifyOthers = tokenService.authorize(token, "PATCH/v1/grapher/user")[1];
        List<Object> claims = TokenUtils.getClaims(token, "name", "sub");

        service.updateUser((String)claims.get(0), (long)claims.get(1), user, canModifyOthers);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(

        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "id") long id
    ) {

        tokenService.authenticate(token);

        boolean canDeleteOthers = tokenService.authorize(token, "DELETE/v1/grapher/user/{id}")[1];
        List<Object> claims = TokenUtils.getClaims(token, "name", "sub");
        long requesterId = (long)claims.get(1);

        if(id == requesterId || (id != requesterId && canDeleteOthers)) {

            service.deleteUser((String)claims.get(0), requesterId);
            return(ResponseEntity.ok().build());
        }

        throw new AuthorizationException(ErrorFactory.generate(ErrorCode.ILLEGAL_ACTION));
    }

    ///
}
