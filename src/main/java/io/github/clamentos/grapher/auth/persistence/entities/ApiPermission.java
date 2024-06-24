package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Entity @Table(name = "API_PERMISSION")

///
public class ApiPermission {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "api_permission_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "api_permission_id_seq", sequenceName = "api_permission_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "path")
    private String path;

    @Column(name = "is_optional")
    private boolean isOptional;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instant_audit_id", referencedColumnName = "id")
    private InstantAudit instantAudit;

    @ManyToOne
    @JoinColumn(name = "operation_id")
    private Operation operation;

    ///
}
