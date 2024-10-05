package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.persistence.AuditAction;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Session;
import io.github.clamentos.grapher.auth.persistence.entities.Subscription;
import io.github.clamentos.grapher.auth.persistence.entities.User;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.SubscriptionRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.UserRepository;

///..
import io.github.clamentos.grapher.auth.web.dtos.SubscriptionDto;

///.
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

///..
import java.util.function.Function;

///..
import java.util.stream.Collectors;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
/**
 * <h3>Subscription Service</h3>
 * Spring {@link Service} to manage {@link Subscription} objects.
*/

///
@Service

///
public class SubscriptionService {

    ///
    private final AuditRepository auditRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ValidatorService validatorService;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public SubscriptionService(

        AuditRepository auditRepository,
        SubscriptionRepository subscriptionRepository,
        UserRepository userRepository,
        ValidatorService validatorService
    ) {

        this.auditRepository = auditRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.validatorService= validatorService;
    }

    ///
    /**
     * Subscribes the calling user to the specified subscription.
     * @param session : The session of the calling user.
     * @param subscription : The target subscription to apply.
     * @throws DataAccessException If any database access errors occur.
     * @throws EntityNotFoundException If the calling user doesn't exist.
     * @throws IllegalArgumentException If {@code subscription} doesn't pass validation.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    @Transactional
    public void subscribe(Session session, SubscriptionDto subscription)
    throws DataAccessException, EntityNotFoundException, IllegalArgumentException, NullPointerException {

        validatorService.validateSubscription(subscription);

        User subscriber = userRepository.findById(session.getUserId()).orElseThrow(() -> {

            return(new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "SubscriptionService::subscribe -> Subscriber not found", session.getUserId()
            )));
        });

        User publisher = userRepository.findById(subscription.getPublisher()).orElseThrow(() -> {

            return(new EntityNotFoundException(ErrorFactory.create(

                ErrorCode.USER_NOT_FOUND, "SubscriptionService::subscribe -> Publisher not found", subscription.getPublisher()
            )));
        });

        long now = System.currentTimeMillis();

        Subscription subscriptionEntity = subscriptionRepository.save(
            
            new Subscription(publisher, subscriber, subscription.getNotify(), now)
        );

        auditRepository.save(new Audit(subscriptionEntity, AuditAction.CREATED, now, session.getUsername(), null));
    }

    ///..
    /**
     * Updates the subscriptions of the calling user with the specified ones.
     * @param session : The session of the calling user.
     * @param subscriptions : The target subscriptions to modify.
     * @throws DataAccessException If any database access errors occur.
     * @throws EntityNotFoundException If the calling user doesn't exist.
     * @throws IllegalArgumentException If {@code subscriptions} doesn't pass validation.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    @Transactional
    public void toggleNotifications(Session session, Collection<SubscriptionDto> subscriptions)
    throws DataAccessException, EntityNotFoundException, IllegalArgumentException, NullPointerException {

        validatorService.validateSubscriptions(subscriptions);

        Map<Long, Subscription> fetchedSubscriptions = subscriptionRepository.findAllByIdsAndUser(

            subscriptions.stream().map(e -> e.getId()).toList(),
            session.getUserId()
        )
        .stream()
        .collect(Collectors.toMap(e -> e.getId(), Function.identity()));

        List<Audit> audits = new ArrayList<>();
        long now = System.currentTimeMillis();

        for(SubscriptionDto subscription : subscriptions) {

            Subscription subscriptionEntity = fetchedSubscriptions.get(subscription.getId());

            if(subscriptionEntity == null) {

                throw new EntityNotFoundException(ErrorFactory.create(

                    ErrorCode.SUBSCRIPTION_NOT_FOUND,
                    "SubscriptionService::toggleNotifications -> Subscription not found",
                    subscription.getId()
                ));
            }

            subscriptionEntity.setNotify(subscription.getNotify() != null ? subscription.getNotify() : false);
            subscriptionEntity.setUpdatedAt(now);

            audits.add(new Audit(subscriptionEntity, AuditAction.UPDATED, now, session.getUsername(), "notify,updated_at"));
        }

        subscriptionRepository.saveAll(fetchedSubscriptions.values());
        auditRepository.saveAll(audits);
    }

    ///..
    /**
     * Unsubscribes the calling user from the specified subscriptions.
     * @param session : The session of the calling user.
     * @param subscriptionIds : The target subscription ids.
     * @throws DataAccessException If any database access errors occur.
     * @throws IllegalArgumentException If {@code subscriptionIds} doesn't pass validation.
     * @throws NullPointerException If {@code session} is {@code null}.
    */
    @Transactional
    public void unsubscribe(Session session, List<Long> subscriptionIds)
    throws DataAccessException, IllegalArgumentException, NullPointerException {

        validatorService.requireFullyFilled(subscriptionIds, "body", "id");

        List<Subscription> fetchedSubscriptions = subscriptionRepository.findAllByIdsAndUser(subscriptionIds, session.getUserId());
        List<Audit> audits = new ArrayList<>();
        long now = System.currentTimeMillis();

        for(Subscription subscription : fetchedSubscriptions) {

            audits.add(new Audit(subscription, AuditAction.DELETED, now, session.getUsername(), null));
        }

        subscriptionRepository.deleteAll(fetchedSubscriptions);
        auditRepository.saveAll(audits);
    }

    ///
}
