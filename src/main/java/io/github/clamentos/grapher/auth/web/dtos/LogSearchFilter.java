package io.github.clamentos.grapher.auth.web.dtos;

///
import java.util.List;

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
public final class LogSearchFilter {

    ///
    private Integer pageNumber;
    private Integer pageSize;

    ///..
    private Long timestampStart;
    private Long timestampEnd;
    private List<String> levels;
    private List<String> threads;
    private String messageLike;
    private Long createdAtStart;
    private Long createdAtEnd;

    ///
}
