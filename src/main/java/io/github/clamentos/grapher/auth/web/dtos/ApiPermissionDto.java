package io.github.clamentos.grapher.auth.web.dtos;

///
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)

///
public final class ApiPermissionDto extends AuditedObject {

    ///
    private Long id;
    private String path;
    private Boolean isOptional;
    private OperationDto operation;

    ///
}
