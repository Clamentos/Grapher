package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Subscription;

///.
import java.util.List;

///.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.stereotype.Repository;

///
/**
 * <h3>Subscription Repository</h3>
 * JPA {@link Repository} for the {@link Subscription} entity.
*/

///
@Repository

///
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    ///
    /**
     * Finds all the subscriptions that match the given id and user id.
     * @param ids : The target ids to search against. 
     * @param userId : The target user id to further filter the search.
     * @return The never {@code null} list of found subscription entities.
    */
    @Query("SELECT s FROM Subscription AS s INNER JOIN s.subscriber AS ss WHERE s.id IN ?1 AND ss.id = ?2")
    List<Subscription> findAllByIdsAndUser(Iterable<Long> ids, long userId);

    ///
}
