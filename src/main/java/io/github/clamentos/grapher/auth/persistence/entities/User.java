package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

///.
import java.util.List;

///.
import lombok.Getter;
import lombok.Setter;

///
@Getter @Setter
@Entity @Table(name = "GRAPHER_USER")

///
public class User {

    ///
    @Id @Column(name = "id")
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "flags")
    private short flags;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instant_audit_id", referencedColumnName = "id")
    private InstantAudit instantAudit;

    ///..
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserOperation> operations;

    ///
}
