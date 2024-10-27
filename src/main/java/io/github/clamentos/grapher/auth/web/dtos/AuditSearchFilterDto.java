package io.github.clamentos.grapher.auth.web.dtos;

///
import io.github.clamentos.grapher.auth.persistence.AuditAction;

///.
import java.util.Set;

///.
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>Audit Search Filter Dto</h3>
 * Ingoing-only DTO for audit search HTTP requests.
*/

///
@Getter
@Setter

///
public final class AuditSearchFilterDto {

    ///
    private Integer pageNumber;
    private Integer pageSize;

    ///..
    private Long recordId;
    private Set<String> tableNames;
    private Set<AuditAction> auditActions;
    private Long createdAtStart;
    private Long createdAtEnd;
    private Set<String> createdByNames;

    ///
}
