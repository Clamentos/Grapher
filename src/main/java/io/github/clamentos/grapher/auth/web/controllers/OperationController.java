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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(

    path = "/v1/grapher/operation",
    consumes = "application/json",
    produces = "application/json"
)

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
    @PostMapping
    public ResponseEntity<Void> createOperations(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody List<String> operations
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "C-OPERATION");
        service.createOperations((String)TokenUtils.getClaims(token, "name").get(0), operations);

        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping
    public ResponseEntity<List<OperationDto>> readOperations(@RequestHeader(name = "Authorization") String token) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "R-OPERATION");

        return(ResponseEntity.ok(service.readOperations()));
    }

    ///..
    @PutMapping
    public ResponseEntity<Void> updateOperation(

        @RequestHeader(name = "Authorization") String token,
        @RequestBody OperationDto operation
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "U-OPERATION");
        service.updateOperation((String)TokenUtils.getClaims(token, "name").get(0), operation);

        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteOperation(

        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "id") short id
    ) {

        tokenService.authenticate(token);
        tokenService.authorize(token, "D-OPERATION");
        service.deleteOperation((String)TokenUtils.getClaims(token, "name").get(0), id);

        return(ResponseEntity.ok().build());
    }

    ///
}
