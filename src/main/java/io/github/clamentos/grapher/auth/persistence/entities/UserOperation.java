package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity @Table(name = "USER_OPERATION")

///
public class UserOperation {

    ///
    @Id @Column(name = "id")
    @GeneratedValue(generator = "user_operation_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_operation_id_seq", sequenceName = "user_operation_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "operation_id")
    private Operation operation;

    ///
}
