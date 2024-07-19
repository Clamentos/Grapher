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
import java.time.ZonedDateTime;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Entity @Table(name = "LOG")

///
public class Log {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "log_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "log_id_seq", sequenceName = "log_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "timestamp")
    private ZonedDateTime timestamp;

    @Column(name = "level")
    private String level;

    @Column(name = "thread")
    private String thread;

    @Column(name = "logger")
    private String logger;

    @Column(name = "message")
    private String message;

    ///
}
