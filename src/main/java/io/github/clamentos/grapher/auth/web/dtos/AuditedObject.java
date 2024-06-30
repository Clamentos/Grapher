package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

///
public class AuditedObject {

    ///
    private Long createdAt;
    private Long updatedAt;
    private String createdBy;
    private String updatedBy;

    ///
}
