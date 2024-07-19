package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.ApiPermissionService;
import io.github.clamentos.grapher.auth.business.services.SessionService;

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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final SessionService sessionService;

    ///
    @Autowired
    public ApiPermissionController(ApiPermissionService service, SessionService sessionService) {

        this.service = service;
        this.sessionService = sessionService;
    }

    ///
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> createPermissions(

        @RequestAttribute(name = "sessionId") String sessionId,
        @RequestBody List<ApiPermissionDto> permissions
    ) {

        service.createPermissions(sessionService.getUserSession(sessionId).getUsername(), permissions);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<ApiPermissionDto>> readPermissions() {

        return(ResponseEntity.ok(service.readPermissions()));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updatePermissions(

        @RequestAttribute(name = "sessionId") String sessionId,
        @RequestBody List<ApiPermissionDto> permissions
    ) {

        service.updatePermissions(sessionService.getUserSession(sessionId).getUsername(), permissions);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping
    public ResponseEntity<Void> deletePermissions(

        @RequestAttribute(name = "sessionId") String sessionId,
        @RequestParam(name = "ids") List<Long> ids
    ) {

        service.deletePermissions(sessionService.getUserSession(sessionId).getUsername(), ids);
        return(ResponseEntity.ok().build());
    }

    ///
}
