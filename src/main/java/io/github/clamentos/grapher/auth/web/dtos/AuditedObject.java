package io.github.clamentos.grapher.auth.web.dtos;

///
import com.fasterxml.jackson.annotation.JsonInclude;

///..
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)

///
public class AuditedObject {

    ///
    private Long createdAt;
    private Long updatedAt;
    private String createdBy;
    private String updatedBy;

    ///
}
