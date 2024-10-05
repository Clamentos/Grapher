package io.github.clamentos.grapher.auth.web.dtos;

import io.github.clamentos.grapher.auth.persistence.AuditAction;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public final class AuditSearchFilter {

    private Integer pageNumber;
    private Integer pageSize;

    private Long recordId;
    private List<String> tableNames;
    private List<AuditAction> auditActions;
    private Long createdAtStart;
    private Long createdAtEnd;
    private List<String> createdByNames;
}
