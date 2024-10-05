package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
/**
 * <h3>Subscription</h3>
 * JPA {@link Entity} for the {@code SUBSCRIPTION} database table.
*/

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "SUBSCRIPTION")

///
public class Subscription {

    ///
    public final static transient String COLUMNS = "id,publisher,subscriber,notify,created_at,updated_at";
    public final static transient String TABLE_NAME = "SUBSCRIPTION";

    ///.
    @Id @Column(name = "id")
    @GeneratedValue(generator = "subscription_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "subscription_id_seq", sequenceName = "subscription_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "publisher")
    private User publisher;

    @ManyToOne
    @JoinColumn(name = "subscriber")
    private User subscriber;

    @Column(name = "notify")
    private boolean notify;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;
    
    ///
    /**
     * Instantiates a new {@link Subscription} object.
     * @param publisher : The target publishing user.
     * @param subscriber : The target subscribing user.
     * @param notify : The notify flag.
     * @param initTimestamp : The UNIX timestamp used for initialization.
    */
    public Subscription(User publisher, User subscriber, boolean notify, long initTimestamp) {

        this.publisher = publisher;
        this.subscriber = subscriber;
        this.notify = notify;

        createdAt = initTimestamp;
        updatedAt = initTimestamp;
    }

    ///
}
