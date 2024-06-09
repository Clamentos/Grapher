package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

///.
import java.util.List;

///.
import lombok.Getter;
import lombok.Setter;

///
@Getter @Setter
@Entity @Table(name = "USER")

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

    ///.
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(

        name = "USER_OPERATION", 
        joinColumns = { @JoinColumn(name = "user_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "operation_id") }
    )
    private List<Operation> operations;

    ///
}
