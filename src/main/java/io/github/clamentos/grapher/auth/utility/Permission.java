package io.github.clamentos.grapher.auth.utility;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public final class Permission {

    ///
    private final short operationId;
    private final boolean optional;

    ///
}
