package io.github.clamentos.grapher.auth.persistence.entities;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
/**
 * <h3>Session</h3>
 * JPA {@link Entity} for the {@code SESSION} database table.
*/

///
@Entity
@Table(name = "session")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

///
public class Session {

    ///
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(name = "expires_at")
    private long expiresAt;

    ///
}
