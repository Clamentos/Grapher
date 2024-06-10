package io.github.clamentos.grapher.auth.utility;

import io.github.clamentos.grapher.auth.exceptions.ValidationException;
import io.github.clamentos.grapher.auth.web.dtos.AuditedObject;

public final class Validator {

    public static void requireNull(Object obj, String name) throws ValidationException {

        if(obj != null) throw new ValidationException("Validator.requireNull -> The field \"" + name + "\" must be null.");
    }

    public static void requireNotNull(Object obj, String name) throws ValidationException {

        if(obj == null) throw new ValidationException("Validator.requireNotNull -> The field \"" + name + "\" cannot be null.");
    }

    public static void requireFilled(String str, String name) throws ValidationException {

        if(str == null || str.length() == 0)
            throw new ValidationException("Validator.requireFilled -> The field \"" + name + "\" must not be null nor empty.");
    }

    public static void validateAuditedObject(AuditedObject auditedObject) throws ValidationException {

        requireNull(auditedObject.getCreatedAt(), "createdAt");
        requireNull(auditedObject.getUpdatedAt(), "updatedAt");
        requireNull(auditedObject.getCreatedBy(), "createdBy");
        requireNull(auditedObject.getUpdatedBy(), "updatedBy");
    }
}
