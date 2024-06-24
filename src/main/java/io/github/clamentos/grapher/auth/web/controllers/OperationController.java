package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.OperationService;
import io.github.clamentos.grapher.auth.business.services.TokenService;

///..
import io.github.clamentos.grapher.auth.utility.TokenUtils;

///..
import io.github.clamentos.grapher.auth.web.dtos.OperationDto;

///.
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.http.ResponseEntity;

///.
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
@RequestMapping(path = "/v1/grapher/operation")

///
public final class OperationController {

    ///
    private final OperationService service;
    private final TokenService tokenService;

    ///
    @Autowired
    public OperationController(OperationService service, TokenService tokenService) {

        this.service = service;
        this.tokenService = tokenService;
    }

    ///
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> createOperations(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody List<String> operations
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "POST/v1/grapher/operation");

        service.createOperations((String)TokenUtils.getClaims(token, "name").get(0), operations);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<OperationDto>> readOperations(@RequestHeader(name = "Authorization") String token) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "GET/v1/grapher/operation");

        return(ResponseEntity.ok(service.readOperations()));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updateOperations(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody List<OperationDto> operations
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "PATCH/v1/grapher/operation");

        service.updateOperations((String)TokenUtils.getClaims(token, "name").get(0), operations);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping
    public ResponseEntity<Void> deleteOperations(

        @RequestHeader(name = "Authorization") String token,
        @RequestParam(name = "ids") List<Short> ids
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "DELETE/v1/grapher/operation");

        service.deleteOperations((String)TokenUtils.getClaims(token, "name").get(0), ids);
        return(ResponseEntity.ok().build());
    }

    ///
}
