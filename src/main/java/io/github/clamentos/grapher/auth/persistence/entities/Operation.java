package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

///.
import java.util.List;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity @Table(name = "OPERATION")

///
public class Operation {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "operation_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "operation_id_seq", sequenceName = "operation_id_seq", allocationSize = 1)
    private short id;

    @Column(name = "name")
    private String name;

    ///..
    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL)
    private List<UserOperation> userOperations;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instant_audit_id", referencedColumnName = "id")
    private InstantAudit instantAudit;

    ///
}
