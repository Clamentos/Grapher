package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity @Table(name = "INSTANT_AUDIT")

///
public class InstantAudit {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "instant_audit_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "instant_audit_id_seq", sequenceName = "instant_audit_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "updated_at")
    private long updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    ///
}
