package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.SubscriptionService;

///..
import io.github.clamentos.grapher.auth.error.exceptions.IllegalActionException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///..
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;

///.
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.Set;

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
/**
 * <h3>Subscription Controller</h3>
 * Spring {@link RestController} that exposes user subscription management APIs.
*/

///
@RestController
@RequestMapping(path = "/grapher/v1/auth-service/user/subscriptions")

///
public class SubscriptionController {

    ///
    private final SubscriptionService subscriptionService;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    ///
    /**
     * Subscribes the calling user to the specified subscription.
     * @param session : The session of the calling user.
     * @param subscription : The target subscription to apply.
     * @throws DataAccessException If any database access errors occur.
     * @throws EntityNotFoundException If the calling user doesn't exist.
     * @throws IllegalActionException If the target publisher is not a creator.
     * @throws IllegalArgumentException If {@code subscription} doesn't pass validation.
    */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> subscribe(@RequestAttribute(name = "session") Session session, @RequestBody SubscriptionDto subscription)
    throws DataAccessException, EntityNotFoundException, IllegalActionException, IllegalArgumentException {

        subscriptionService.subscribe(session, subscription);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Updates the subscriptions of the calling user with the specified ones.
     * @param session : The session of the calling user.
     * @param subscriptions : The target subscriptions to modify.
     * @throws DataAccessException If any database access errors occur.
     * @throws EntityNotFoundException If the calling user doesn't exist.
     * @throws IllegalArgumentException If {@code subscriptions} doesn't pass validation.
    */
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> toggleNotifications(

        @RequestAttribute(name = "session") Session session,
        @RequestBody Set<SubscriptionDto> subscriptions
    
    ) throws DataAccessException, EntityNotFoundException, IllegalArgumentException {

        subscriptionService.toggleNotifications(session, subscriptions);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Unsubscribes the calling user from the specified subscriptions.
     * @param session : The session of the calling user.
     * @param subscriptionIds : The target subscription ids.
     * @throws DataAccessException If any database access errors occur.
     * @throws IllegalArgumentException If {@code subscriptionIds} doesn't pass validation.
    */
    @DeleteMapping(consumes = "application/json")
    public ResponseEntity<Void> unsubscribe(@RequestAttribute(name = "session") Session session, @RequestBody Set<Long> subscriptionIds)
    throws DataAccessException, IllegalArgumentException {

        subscriptionService.unsubscribe(session, subscriptionIds);
        return(ResponseEntity.ok().build());
    }

    ///
}
