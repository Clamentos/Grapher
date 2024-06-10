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
import lombok.Getter;
import lombok.Setter;

///
@Getter @Setter
@Entity @Table(name = "AUDIT")

///
public class Audit {

    ///
    @Id
    @GeneratedValue(generator = "audit_id_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "audit_id_sequence", allocationSize = 50)
    @Column(name = "id")
    private long id;

    @Column(name = "recordId")
    private long recordId;

    @Column(name = "table")
    private String table;

    @Column(name = "columns")
    private String columns;

    @Column(name = "action")
    private char action;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "created_by")
    private String createdBy;

    ///
}
