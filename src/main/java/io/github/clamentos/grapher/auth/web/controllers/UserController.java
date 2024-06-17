package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.TokenService;
import io.github.clamentos.grapher.auth.business.services.UserService;

///..
import io.github.clamentos.grapher.auth.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.utility.ErrorCode;
import io.github.clamentos.grapher.auth.utility.ErrorFactory;
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
@RequestMapping(

    path = "/v1/grapher/user",
    consumes = "application/json",
    produces = "application/json"
)

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
    @PostMapping(path = "/register")
    public ResponseEntity<Void> register(@RequestBody UserDto user) {

        service.register(user);
        return(ResponseEntity.ok().build());
    }

    ///..
    @PostMapping(path = "/login")
    public ResponseEntity<AuthDto> login(@RequestBody UsernamePassword credentials) {

        return(ResponseEntity.ok(service.login(credentials)));
    }

    ///..
    @DeleteMapping(path = "/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization") String token) {

        tokenService.blacklistToken(token);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(

        @RequestParam(name = "username", required = false, defaultValue = "") String username,
        @RequestParam(name = "email", required = false, defaultValue = "") String email,
        @RequestParam(name = "createdAtRange", required = false, defaultValue = "") String createdAtRange,
        @RequestParam(name = "updatedAtRange", required = false, defaultValue = "") String updatedAtRange,
        @RequestParam(name = "operations", required = false, defaultValue = "") String operations
    ) {

        // TODO: filters
        return null;
    }

    ///..
    @PatchMapping
    public ResponseEntity<Void> updateUser(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody UserDto user
    ) {

        tokenService.authenticate(token);

        boolean canModifyOthers = tokenService.authorize(token, "U-USER-SELF", "#U-USER-OTHER")[1];
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

        boolean canDeleteOthers = tokenService.authorize(token, "D-USER-SELF", "#D-USER-OTHER")[1];
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
