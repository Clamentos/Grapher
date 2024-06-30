package io.github.clamentos.grapher.auth.parameters.operation;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum CreateOperationInvalidDto {

    ///
    DTO_0("[\"TEST-OP-11\",\"TEST-OP-12\",\"\",\"TEST-OP-14\",\"TEST-OP-15\",\"TEST-OP-16\"]");

    ///
    private final String dto;

    ///
}
