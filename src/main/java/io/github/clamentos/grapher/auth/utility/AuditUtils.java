package io.github.clamentos.grapher.auth.utility;

///
import io.github.clamentos.grapher.auth.persistence.entities.ApiPermission;
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Operation;
import io.github.clamentos.grapher.auth.persistence.entities.User;
import io.github.clamentos.grapher.auth.persistence.entities.UserOperation;

///.
import java.util.ArrayList;
import java.util.List;

///
public final class AuditUtils {

    ///
    public static List<Audit> insertUserAudit(User user) {

        List<Audit> audits = new ArrayList<>();
        long now = user.getInstantAudit().getCreatedAt();
        String username = user.getInstantAudit().getCreatedBy();

        audits.add(new Audit(0, user.getId(), "GRAPHER_USER", "id,username,password,email,flags,instant_audit_id", 'C', now, username));
        audits.add(instantAuditAudit(user.getInstantAudit().getId(), 'C', now, username));

        return(audits);
    }

    ///..
    public static List<Audit> updateUserAudit(User user, List<UserOperation> userOperations, String columns) {

        List<Audit> audits = new ArrayList<>();
        long now = user.getInstantAudit().getUpdatedAt();
        String username = user.getInstantAudit().getUpdatedBy();

        audits.add(new Audit(0, user.getId(), "GRAPHER_USER", columns, 'U', now, username));

        for(UserOperation userOperation : userOperations) {

            audits.add(new Audit(0, userOperation.getId(), "USER_OPERATION", "id,user_id,operation_id", 'C', now, username));
        }

        audits.add(instantAuditAudit(user.getInstantAudit().getId(), 'U', now, username));
        return(audits);
    }

    ///..
    public static List<Audit> deleteUserAudit(User user, long deletedAt, String deletedBy) {

        List<Audit> audits = new ArrayList<>();

        audits.add(new Audit(

            0, user.getId(),
            "GRAPHER_USER",
            "id,username,password,email,flags,instant_audit_id",
            'D',
            deletedAt,
            deletedBy
        ));

        for(UserOperation userOperation : user.getOperations()) {

            audits.add(new Audit(0, userOperation.getId(), "USER_OPERATION", "id,user_id,operation_id", 'D', deletedAt, deletedBy));
        }

        audits.add(instantAuditAudit(user.getInstantAudit().getId(), 'D', deletedAt, deletedBy));
        return(audits);
    }

    ///..
    public static List<Audit> insertOperationAudit(Operation operation) {

        List<Audit> audits = new ArrayList<>();
        long now = operation.getInstantAudit().getCreatedAt();
        String username = operation.getInstantAudit().getCreatedBy();

        audits.add(new Audit(0, operation.getId(), "OPERATION", "id,name,instant_audit_id", 'C', now, username));
        audits.add(instantAuditAudit(operation.getInstantAudit().getId(), 'C', now, username));

        return(audits);
    }

    ///..
    public static List<Audit> updateOperationAudit(Operation operation) {

        List<Audit> audits = new ArrayList<>();
        long now = operation.getInstantAudit().getUpdatedAt();
        String username = operation.getInstantAudit().getUpdatedBy();

        audits.add(new Audit(0, operation.getId(), "OPERATION", "name", 'U', now, username));
        audits.add(instantAuditAudit(operation.getInstantAudit().getId(), 'U', now, username));

        return(audits);
    }

    ///..
    public static List<Audit> deleteOperationAudit(Operation operation, long deletedAt, String deletedBy) {

        List<Audit> audits = new ArrayList<>();

        audits.add(new Audit(0, operation.getId(), "OPERATION", "id,name,instant_audit_id", 'D', deletedAt, deletedBy));
        audits.add(instantAuditAudit(operation.getInstantAudit().getId(), 'D', deletedAt, deletedBy));

        return(audits);
    }

    ///..
    public static List<Audit> insertApiPermissionAudit(ApiPermission permission) {

        List<Audit> audits = new ArrayList<>();
        long now = permission.getInstantAudit().getCreatedAt();
        String username = permission.getInstantAudit().getCreatedBy();

        audits.add(new Audit(

            0,
            permission.getId(),
            "API_PERMISSION",
            "id,path,is_optional,instant_audit_id,operation_id",
            'C',
            now,
            username
        ));

        audits.add(instantAuditAudit(permission.getInstantAudit().getId(), 'C', now, username));
        return(audits);
    }

    ///..
    public static List<Audit> updateApiPermissionAudit(ApiPermission permission, String columns) {

        List<Audit> audits = new ArrayList<>();
        long now = permission.getInstantAudit().getUpdatedAt();
        String username = permission.getInstantAudit().getUpdatedBy();

        audits.add(new Audit(0, permission.getId(), "API_PERMISSION", columns, 'U', now, username));
        audits.add(instantAuditAudit(permission.getInstantAudit().getId(), 'C', now, username));

        return(audits);
    }

    ///..
    public static List<Audit> deleteApiPermissionAudit(ApiPermission permission, long deletedAt, String deletedBy) {

        List<Audit> audits = new ArrayList<>();

        audits.add(new Audit(

            0,
            permission.getId(),
            "API_PERMISSION",
            "id,path,is_optional,instant_audit_id,operation_id",
            'D',
            deletedAt,
            deletedBy
        ));

        audits.add(instantAuditAudit(permission.getInstantAudit().getId(), 'U', deletedAt, deletedBy));
        return(audits);
    }

    ///.
    private static Audit instantAuditAudit(long recordId, char action, long createdAt, String createdBy) {

        String columns = switch(action) {

            case 'C', 'D': yield "id,created_at,updated_at,created_by,updated_by";
            case 'U': yield "updated_at,updated_by";
            default: throw new IllegalArgumentException();
        };

        return(new Audit(0, recordId, "INSTANT_AUDIT", columns, action, createdAt, createdBy));
    }

    ///
}
