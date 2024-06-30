package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.OperationService;

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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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

    ///
    @Autowired
    public OperationController(OperationService service) {

        this.service = service;
    }

    ///
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> createOperations(

        @RequestAttribute(name = "jwtToken") String jwtToken,
        @RequestBody List<String> operations
    ) {

        service.createOperations((String)TokenUtils.getClaims(jwtToken, "name").get(0), operations);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<OperationDto>> readOperations(@RequestAttribute(name = "jwtToken") String jwtToken) {

        return(ResponseEntity.ok(service.readOperations()));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updateOperations(

        @RequestAttribute(name = "jwtToken") String jwtToken,
        @RequestBody List<OperationDto> operations
    ) {

        service.updateOperations((String)TokenUtils.getClaims(jwtToken, "name").get(0), operations);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping
    public ResponseEntity<Void> deleteOperations(

        @RequestAttribute(name = "jwtToken") String jwtToken,
        @RequestParam(name = "ids") List<Short> ids
    ) {

        service.deleteOperations((String)TokenUtils.getClaims(jwtToken, "name").get(0), ids);
        return(ResponseEntity.ok().build());
    }

    ///
}
