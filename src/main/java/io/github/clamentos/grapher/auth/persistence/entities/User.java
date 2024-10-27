package io.github.clamentos.grapher.auth.persistence.entities;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
/**
 * <h3>User</h3>
 * JPA {@link Entity} for the {@code GRAPHER_USER} database table.
*/

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "GRAPHER_USER")

///
public class User {

    ///
    public static final String COLUMNS =

        "id,username,password,email,profile_picture,about,role,failed_accesses,locked_until," +
        "lock_reason,password_last_changed_at,created_at,created_by,updated_at,updated_by"
    ;

    public static final String TABLE_NAME = "GRAPHER_USER";

    ///.
    @Id @Column(name = "id")
    @GeneratedValue(generator = "grapher_user_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "grapher_user_id_seq", sequenceName = "grapher_user_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "about")
    private String about;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "failed_accesses")
    private short failedAccesses;

    @Column(name = "locked_until")
    private long lockedUntil;

    @Column(name = "lock_reason")
    private String lockReason;

    @Column(name = "password_last_changed_at")
    private long passwordLastChangedAt;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private long updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    ///..
    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private List<Subscription> subscribers;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

    ///
    /**
     * Instantiates a new {@link User} object for registration.
     * @param username : The username.
     * @param password : The hashed password.
     * @param email : The email.
     * @param profilePicture : The profile picture.
     * @param about : The about text.
     * @param creator : The username of the creator.
     * @apiNote Some fields are populated with the following values:
     * <ul>
     *   <li>{@link System#currentTimeMillis} as creation, update, password expire and lock timestamps.</li>
     *   <li>{@link UserRole#getDefault} as the role.</li>
     * </ul>
    */
    public User(String username, String password, String email, byte[] profilePicture, String about, String creator) {

        long now = System.currentTimeMillis();

        this.username = username;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.about = about;

        role = UserRole.getDefault();
        failedAccesses = 0;
        lockedUntil = now;
        lockReason = "";
        passwordLastChangedAt = now;
        createdAt = now;
        createdBy = creator;
        updatedAt = now;
        updatedBy = creator;
    }

    ///..
    /**
     * Instantiates a new {@link User} object for search.
     * @param id : The user id.
     * @param username : The username.
     * @param email : The email.
     * @param role : The user role.
     * @param failedAccesses : The failed logins count.
     * @param lockedUntil : The account lock UNIX timestamp.
     * @param lockReason : The account lock reason.
     * @param passwordLastChangedAt : The password last changed UNIX timestamp.
     * @param createdAt : The creation UNIX timestamp.
     * @param createdBy : The creator username.
     * @param updatedAt : The update UNIX timestamp.
     * @param updatedBy : The username of the updating user.
     * @apiNote This constructor is currently only used for JPA queries.
    */
    public User(

        long id,
        String username,
        String email,
        UserRole role,
        short failedAccesses,
        long lockedUntil,
        String lockReason,
        long passwordLastChangedAt,
        long createdAt,
        String createdBy,
        long updatedAt,
        String updatedBy
    ) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.failedAccesses = failedAccesses;
        this.lockedUntil = lockedUntil;
        this.lockReason = lockReason;
        this.passwordLastChangedAt = passwordLastChangedAt;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    ///
}
