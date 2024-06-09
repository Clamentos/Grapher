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
@Entity @Table(name = "AUDIT")

///
public class Audit {

    ///
    @Id @Column(name = "id")
    private long id;

    @Column(name = "recordId")
    private long recordId;

    @Column(name = "table")
    private String table;

    @Column(name = "columns")
    private String columns;

    @Column(name = "action")
    private byte action;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "created_by")
    private String createdBy;

    ///
}
