package io.github.clamentos.grapher.auth.web.dtos;

///
import java.util.List;

///.
import lombok.Getter;
import lombok.Setter;

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
    private String message;
    private Long createdAtStart;
    private Long createdAtEnd;

    ///
}
