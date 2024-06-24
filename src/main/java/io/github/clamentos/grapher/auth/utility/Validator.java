package io.github.clamentos.grapher.auth.utility;

///
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditedObject;

///
public final class Validator {

    ///
    public static void requireNull(Object obj, String name) throws IllegalArgumentException {

        if(obj != null) throw new IllegalArgumentException(ErrorFactory.generate(ErrorCode.VALIDATOR_REQUIRE_NULL, name));
    }

    ///..
    public static void requireNotNull(Object obj, String name) throws IllegalArgumentException {

        if(obj == null) throw new IllegalArgumentException(ErrorFactory.generate(ErrorCode.VALIDATOR_REQUIRE_NOT_NULL, name));
    }

    ///..
    public static void requireFilled(String str, String name) throws IllegalArgumentException {

        if(str == null || str.length() == 0)
            throw new IllegalArgumentException(ErrorFactory.generate(ErrorCode.VALIDATOR_REQUIRE_FILLED, name));
    }

    ///..
    public static void validateAuditedObject(AuditedObject auditedObject) throws IllegalArgumentException {

        requireNull(auditedObject.getCreatedAt(), "createdAt");
        requireNull(auditedObject.getUpdatedAt(), "updatedAt");
        requireNull(auditedObject.getCreatedBy(), "createdBy");
        requireNull(auditedObject.getUpdatedBy(), "updatedBy");
    }

    ///
}
