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
public final class AuditDto {

    ///
    private long id;
    private long recordId;
    private String tableName;
    private String columns;
    private char action;
    private long createdAt;
    private String createdBy;

    ///
}
