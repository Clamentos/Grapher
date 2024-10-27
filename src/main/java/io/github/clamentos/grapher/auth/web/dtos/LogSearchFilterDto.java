package io.github.clamentos.grapher.auth.web.dtos;

///
import java.util.Set;

///.
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>Log Search Filter Dto</h3>
 * Ingoing-only DTO for log search HTTP requests.
*/

///
@Getter
@Setter

///
public final class LogSearchFilterDto {

    ///
    private Integer pageNumber;
    private Integer pageSize;

    ///..
    private Long timestampStart;
    private Long timestampEnd;
    private Set<String> levels;
    private Set<String> threads;
    private String messageLike;
    private Long createdAtStart;
    private Long createdAtEnd;

    ///
}
