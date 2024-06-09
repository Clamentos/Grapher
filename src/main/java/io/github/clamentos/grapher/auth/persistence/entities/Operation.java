package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

///.
import lombok.Getter;
import lombok.Setter;

///
@Getter @Setter
@Entity @Table(name = "OPERATION")

///
public class Operation {

    ///
    @Id @Column(name = "id")
    private short id;

    @Column(name = "name")
    private String name;

    ///
}
