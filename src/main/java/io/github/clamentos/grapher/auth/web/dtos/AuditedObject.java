package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter

///
public class AuditedObject {

    ///
    private long createdAt;
    private long updatedAt;
    private String createdBy;
    private String updatedBy;

    ///
}
