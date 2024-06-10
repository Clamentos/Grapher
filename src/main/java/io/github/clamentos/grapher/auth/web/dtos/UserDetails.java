package io.github.clamentos.grapher.auth.web.dtos;

///
import java.util.List;

///.
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter

///
public final class UserDetails extends AuditedObject {

    ///
    private Long id;
    private String username;
    private String password;
    private String email;
    private Short flags;

    private List<String> operations;

    ///
}
