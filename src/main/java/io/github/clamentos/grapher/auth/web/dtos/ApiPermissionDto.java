package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter

///
public final class ApiPermissionDto extends AuditedObject {

    ///
    private Long id;
    private String path;
    private Boolean isOptional;
    private OperationDto operation;

    ///
}
