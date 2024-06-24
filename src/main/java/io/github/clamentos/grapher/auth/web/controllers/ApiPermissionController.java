package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.ApiPermissionService;
import io.github.clamentos.grapher.auth.business.services.TokenService;

///..
import io.github.clamentos.grapher.auth.utility.TokenUtils;

///..
import io.github.clamentos.grapher.auth.web.dtos.ApiPermissionDto;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(path = "/v1/grapher/permissions")

///
public final class ApiPermissionController {

    ///
    private final ApiPermissionService service;
    private final TokenService tokenService;

    ///
    @Autowired
    public ApiPermissionController(ApiPermissionService service, TokenService tokenService) {

        this.service = service;
        this.tokenService = tokenService;
    }

    ///
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> createPermissions(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody List<ApiPermissionDto> permissions
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "POST/v1/grapher/permissions");

        service.createPermissions((String)TokenUtils.getClaims(token, "name").get(0), permissions);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ApiPermissionDto>> readPermissions(@RequestHeader(name = "Authorization") String token) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "GET/v1/grapher/permissions");

        return(ResponseEntity.ok(service.readPermissions()));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updatePermissions(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody List<ApiPermissionDto> permissions
    ) {
        tokenService.authenticate(token);
        tokenService.authorize(token, "PATCH/v1/grapher/permissions");

        service.updatePermissions((String)TokenUtils.getClaims(token, "name").get(0), permissions);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping
    public ResponseEntity<Void> deletePermissions(

        @RequestHeader(name = "Authorization") String token,
        @RequestParam(name = "ids", required = true) List<Long> ids
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "DELETE/v1/grapher/permissions");

        service.deletePermissions(token, ids);
        return(ResponseEntity.ok().build());
    }

    ///
}
