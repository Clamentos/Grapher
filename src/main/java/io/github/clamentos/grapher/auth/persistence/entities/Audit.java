package io.github.clamentos.grapher.auth.persistence.entities;

///
import io.github.clamentos.grapher.auth.persistence.AuditAction;

///.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
/**
 * <h3>Audit</h3>
 * JPA {@link Entity} for the {@code AUDIT} database table.
*/

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "AUDIT")

///
public class Audit {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "audit_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "audit_id_seq", sequenceName = "audit_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "record_id")
    private long recordId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "columns")
    private String columns;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "created_by")
    private String createdBy;

    ///
    /**
     * Instantiates a new {@link Audit} object starting from an already existing {@link User} entity.
     * @param user : The user entity to build the audit from.
     * @param action : The audit action.
     * @param createdBy : The audit creation username.
     * @param columns : The audit columns.
    */
    public Audit(User user, AuditAction action, String createdBy, String columns) {

        if(columns == null) this.columns = User.COLUMNS;
        else this.columns = columns;

        this.action = action;
        this.createdBy = createdBy;

        recordId = user.getId();
        tableName = User.TABLE_NAME;

        switch(action) {

            case AuditAction.CREATED: this.createdAt = user.getCreatedAt(); break;
            case AuditAction.UPDATED: this.createdAt = user.getUpdatedAt(); break;
            case AuditAction.DELETED: this.createdAt = System.currentTimeMillis(); break;
        }
    }

    ///..
    /**
     * Instantiates a new {@link Audit} object starting from an already existing {@link Subscription} entity.
     * @param subscription : The subscription entity to build the audit from.
     * @param action : The audit action.
     * @param createdAt : The audit creation UNIX timestamp.
     * @param createdBy : The audit creation username.
    */
    public Audit(Subscription subscription, AuditAction action, long createdAt, String createdBy, String columns) {

        if(columns == null) this.columns = Subscription.COLUMNS;
        else this.columns = columns;

        this.action = action;
        this.createdAt = createdAt;
        this.createdBy = createdBy;

        recordId = subscription.getId();
        tableName = Subscription.TABLE_NAME;
    }

    ///
}
