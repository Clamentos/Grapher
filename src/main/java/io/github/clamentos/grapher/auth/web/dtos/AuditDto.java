package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public final class AuditDto {

    ///
    private final long id;
    private final long recordId;
    private final String tableName;
    private final String columns;
    private final char action;
    private final long createdAt;
    private final String createdBy;

    ///
}
