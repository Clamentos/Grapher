package io.github.clamentos.grapher.auth.web.dtos;

///
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import java.util.Objects;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
/**
 * <h3>Subscription Dto</h3>
 * Bidirectional DTO for user subscription related HTTP requests and responses.
*/

///
@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

///
public final class SubscriptionDto {

    ///
    private Long id;
    private Long publisher;
    private Long subscriber;
    private Boolean notify;
    private Long createdAt;
    private Long updatedAt;

    ///
    @Override
    public boolean equals(Object other) {

        if(this == other) return(true);
        if(!(other instanceof SubscriptionDto)) return(false);

        SubscriptionDto otherSub = (SubscriptionDto)other;
        return(Objects.equals(publisher, otherSub.getPublisher()) && Objects.equals(subscriber, otherSub.getSubscriber()));
    }

    ///..
    @Override
    public int hashCode() {

        long hash = 1;

        hash = hash + (31 * publisher);
        hash = hash + (31 * subscriber);

        return((int)hash);
    }

    ///
}
