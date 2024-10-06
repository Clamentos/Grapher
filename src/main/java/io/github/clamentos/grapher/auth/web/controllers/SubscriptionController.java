package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.SubscriptionService;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///..
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;

///.
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.http.ResponseEntity;

///..
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(path = "/grapher/v1/auth-service/user/subscriptions")

///
public class SubscriptionController {

    ///
    private final SubscriptionService subscriptionService;

    ///
    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    ///
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> subscribe(

        @RequestAttribute(name = "session") Session session,
        @RequestBody SubscriptionDto subscription
    
    ) throws DataAccessException, EntityNotFoundException, IllegalArgumentException {

        subscriptionService.subscribe(session, subscription);
        return(ResponseEntity.ok().build());
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> toggleNotifications(

        @RequestAttribute(name = "session") Session session,
        @RequestBody List<SubscriptionDto> subscriptions
    
    ) throws DataAccessException, EntityNotFoundException, IllegalArgumentException {

        subscriptionService.toggleNotifications(session, subscriptions);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping(consumes = "application/json")
    public ResponseEntity<Void> unsubscribe(@RequestAttribute(name = "session") Session session, @RequestBody List<Long> subscriptionIds)
    throws DataAccessException, IllegalArgumentException {

        subscriptionService.unsubscribe(session, subscriptionIds);
        return(ResponseEntity.ok().build());
    }

    ///
}
