package io.github.clamentos.grapher.auth.persistence.entities;

///
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Entity @Table(name = "BLACKLISTED_TOKEN")

///
public class BlacklistedToken {

    ///
    @Id @Column(name = "hash")
    private String hash;

    @Column(name = "expires_at")
    private long expiresAt;

    ///
}
