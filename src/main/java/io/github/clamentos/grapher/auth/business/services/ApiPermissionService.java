package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.business.mappers.ApiPermissionMapper;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.persistence.entities.ApiPermission;
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.InstantAudit;
import io.github.clamentos.grapher.auth.persistence.entities.Operation;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.ApiPermissionRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.OperationRepository;

///..
import io.github.clamentos.grapher.auth.utility.AuditUtils;
import io.github.clamentos.grapher.auth.utility.Validator;

///..
import io.github.clamentos.grapher.auth.web.dtos.ApiPermissionDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

///..
import java.util.function.Function;

///..
import java.util.stream.Collectors;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
@Service

///
public class ApiPermissionService {

    ///
    private final ApiPermissionRepository repository;
    private final AuditRepository auditRepository;
    private final OperationRepository operationRepository;
    private final ApiPermissionMapper mapper;

    ///
    @Autowired
    public ApiPermissionService(

        ApiPermissionRepository repository,
        AuditRepository auditRepository,
        OperationRepository operationRepository,
        ApiPermissionMapper mapper
    ) {

        this.repository = repository;
        this.auditRepository = auditRepository;
        this.operationRepository = operationRepository;
        this.mapper = mapper;
    }

    ///
    @Transactional
    public void createPermissions(String username, List<ApiPermissionDto> permissions)
    throws EntityExistsException, EntityNotFoundException, IllegalArgumentException {

        Set<Short> operationIds = permissions.stream().map((p) -> { return(p.getOperation().getId()); }).collect(Collectors.toSet());
        List<Operation> existingOperations = operationRepository.findAllById(operationIds);

        if(existingOperations.size() == operationIds.size()) {

            Set<String> desiredPaths = permissions.stream().map((p) -> { return(p.getPath()); }).collect(Collectors.toSet());
            List<Long> conflicts = repository.findAllIdsByCombo(desiredPaths, existingOperations);

            if(conflicts.size() == 0) {

                List<ApiPermission> entities = new ArrayList<>();
                long now = System.currentTimeMillis();

                for(ApiPermissionDto permission : permissions) {

                    validate(permission, false);

                    entities.add(new ApiPermission(

                        0,
                        permission.getPath(),
                        permission.getIsOptional(),
                        new InstantAudit(0, now, now, username, username),
                        new Operation(permission.getOperation().getId(), null, null, null, null)
                    ));
                }

                entities = repository.saveAll(entities);
                List<Audit> audits = new ArrayList<>();

                for(ApiPermission entity : entities) {

                    audits.addAll(AuditUtils.insertApiPermissionAudit(entity));
                }

                auditRepository.saveAll(audits);
            }

            else {

                throw new EntityExistsException(ErrorFactory.generate(ErrorCode.PERMISSION_ALREADY_EXISTS, conflicts));
            }
        }

        else {

            Set<Short> existing = existingOperations.stream().map(Operation::getId).collect(Collectors.toSet());

            throw new EntityNotFoundException(ErrorFactory.generate(

                ErrorCode.PERMISSION_NOT_FOUND,
                operationIds.stream().filter((e) -> existing.contains(e) == false ).toList()
            ));
        }
    }

    ///..
    public List<ApiPermissionDto> readPermissions() {

        return(mapper.mapIntoDtos(repository.findAll()));
    }

    ///..
    @Transactional
    public void updatePermissions(String username, List<ApiPermissionDto> permissions)
    throws EntityNotFoundException, IllegalArgumentException {

        if(permissions.size() > 0) {

            Map<Long, ApiPermission> entities = repository.findAllById(

                permissions
                    .stream()
                    .map(ApiPermissionDto::getId).toList()
            )
            .stream()
            .collect(Collectors.toMap(ApiPermission::getId, Function.identity()));

            if(entities.size() == permissions.size()) {

                List<ApiPermission> entitiesToSave = new ArrayList<>();
                Map<ApiPermission, Short> idMappings = new HashMap<>();
                List<Audit> audits = new ArrayList<>();
                long now = System.currentTimeMillis();

                for(ApiPermissionDto permission : permissions) {

                    validate(permission, true);

                    StringBuilder sb = new StringBuilder();
                    ApiPermission entity = entities.get(permission.getId());

                    if(permission.getPath() != null) {

                        entity.setPath(permission.getPath());
                        sb.append("path,");
                    }

                    if(permission.getIsOptional() != null) {

                        entity.setOptional(permission.getIsOptional());
                        sb.append("is_optional,");
                    }

                    if(permission.getOperation() != null) {

                        idMappings.put(entity, permission.getOperation().getId());
                        sb.append("operation_id,");
                    }

                    entity.getInstantAudit().setUpdatedAt(now);
                    entity.getInstantAudit().setUpdatedBy(username);

                    sb.deleteCharAt(sb.length() - 1);
                    entitiesToSave.add(entity);
                    audits.addAll(AuditUtils.updateApiPermissionAudit(entity, sb.toString()));
                }

                Map<Short, Operation> operations = operationRepository.findAllById(idMappings.values())

                    .stream()
                    .collect(Collectors.toMap(Operation::getId, Function.identity()))
                ;

                if(operations.size() == idMappings.size()) {

                    for(Map.Entry<ApiPermission, Short> idMapping : idMappings.entrySet()) {

                        ApiPermission entityToSave = idMapping.getKey();
                        entityToSave.setOperation(operations.get(idMapping.getValue()));
                    }
    
                    repository.saveAll(entitiesToSave);
                    auditRepository.saveAll(audits);
                }

                else {

                    throw new EntityNotFoundException(ErrorFactory.generate(

                        ErrorCode.OPERATION_NOT_FOUND,
                        idMappings.values().stream().filter((e) -> operations.containsKey(e) == false).toList()
                    ));
                }
            }

            else {

                throw new EntityNotFoundException(ErrorFactory.generate(

                    ErrorCode.PERMISSION_NOT_FOUND,
                    permissions.stream().map(ApiPermissionDto::getId).filter((e) -> entities.keySet().contains(e) == false).toList()
                ));
            }
        }
    }

    ///..
    @Transactional
    public void deletePermissions(String username, List<Long> ids) {

        List<ApiPermission> permissions = repository.findAllById(ids);

        if(permissions.size() == ids.size()) {

            repository.deleteAllById(ids);
            List<Audit> audits = new ArrayList<>();

            for(ApiPermission entity : permissions) {

                audits.addAll(AuditUtils.deleteApiPermissionAudit(entity, System.currentTimeMillis(), username));
            }

            auditRepository.saveAll(audits);
        }

        else {

            ids.removeAll(permissions.stream().map(ApiPermission::getId).toList());
            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.PERMISSION_NOT_FOUND, ids));
        }
    }

    ///.
    private void validate(ApiPermissionDto permission, boolean insertOrUpdate) throws IllegalArgumentException {

        Validator.validateAuditedObject(permission);

        if(insertOrUpdate) { // update

            Validator.requireNotNull(permission.getId(), "id");

            if(permission.getPath() != null) Validator.requireFilled(permission.getPath(), "path");

            if(permission.getOperation() != null) {

                Validator.validateAuditedObject(permission.getOperation());
                Validator.requireNotNull(permission.getOperation().getId(), "operation.id");
                Validator.requireNull(permission.getOperation().getName(), "operation.name");
            }
        }

        else { // insert

            Validator.requireNull(permission.getId(), "id");
            Validator.requireFilled(permission.getPath(), "path");
            Validator.requireNotNull(permission.getIsOptional(), "isOptional");
        }

        if(permission.getOperation() != null) {

            Validator.validateAuditedObject(permission.getOperation());
            Validator.requireNotNull(permission.getOperation().getId(), "operation.id");
            Validator.requireNull(permission.getOperation().getName(), "operation.name");
        }
    }

    ///
}
