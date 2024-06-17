package io.github.clamentos.grapher.auth.web.controllers;

///
import org.springframework.http.ResponseEntity;

///.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(

    path = "/v1/grapher/auth/observability",
    consumes = "application/json",
    produces = "application/json"
)

///
public final class ObservabilityController {

    ///
    @GetMapping
    public ResponseEntity<Void> healthCheck() {

        return(ResponseEntity.ok().build());
    }

    ///
}
