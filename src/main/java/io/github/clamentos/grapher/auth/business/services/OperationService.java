package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.business.mappers.OperationMapper;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.InstantAudit;
import io.github.clamentos.grapher.auth.persistence.entities.Operation;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.OperationRepository;

///..
import io.github.clamentos.grapher.auth.utility.AuditUtils;
import io.github.clamentos.grapher.auth.utility.Validator;

///..
import io.github.clamentos.grapher.auth.web.dtos.OperationDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///.
import java.util.ArrayList;
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
public class OperationService {

    ///
    private final OperationRepository repository;
    private final AuditRepository auditRepository;
    private final OperationMapper mapper;

    ///
    @Autowired
    public OperationService(OperationRepository repository, AuditRepository auditRepository, OperationMapper mapper) {

        this.repository = repository;
        this.auditRepository = auditRepository;
        this.mapper = mapper;
    }

    ///
    @Transactional
    public void createOperations(String username, List<String> operations) throws EntityExistsException, IllegalArgumentException {

        List<String> existingOperations = repository.doNamesExist(operations);

        if(existingOperations.size() == 0) {

            List<Operation> entities = new ArrayList<>();
            long now = System.currentTimeMillis();

            for(String operation : operations) {

                Validator.requireFilled(operation, "operation");
                entities.add(new Operation((short)0, operation, new InstantAudit(0, now, now, username, username), null, null));
            }

            entities = repository.saveAll(entities);

            List<Audit> audits = new ArrayList<>();
            entities.forEach(e -> audits.addAll(AuditUtils.insertOperationAudit(e)));

            auditRepository.saveAll(audits);
        }

        else {

            throw new EntityExistsException(ErrorFactory.generate(ErrorCode.OPERATION_ALREADY_EXISTS, existingOperations));
        }
    }

    ///..
    public List<OperationDto> readOperations() {

        return(mapper.mapIntoDtos(repository.findAll()));
    }

    ///..
    @Transactional
    public void updateOperations(String username, List<OperationDto> operations)
    throws EntityExistsException, EntityNotFoundException, IllegalArgumentException {

        List<String> existingOperations = repository.doNamesExist(operations.stream().map(OperationDto::getName).toList());

        if(existingOperations.size() == 0) {

            Map<Short, Operation> entities = repository.findAllById(

                operations.stream().map(OperationDto::getId).toList()
            )
            .stream()
            .collect(Collectors.toMap(Operation::getId, Function.identity()));

            if(entities.size() == operations.size()) {

                List<Operation> modifiedEntities = new ArrayList<>();
                List<Audit> audits = new ArrayList<>();

                for(OperationDto operation : operations) {

                    Validator.requireFilled(operation.getName(), "name");
                    Operation entity = entities.get(operation.getId());

                    entity.setName(operation.getName());
                    entity.getInstantAudit().setUpdatedAt(System.currentTimeMillis());
                    entity.getInstantAudit().setUpdatedBy(username);

                    modifiedEntities.add(entity);
                    audits.addAll(AuditUtils.updateOperationAudit(entity));
                }

                repository.saveAll(modifiedEntities);
                auditRepository.saveAll(audits);
            }

            else {

                Set<String> foundNames = entities.values().stream().map(Operation::getName).collect(Collectors.toSet());

                throw new EntityNotFoundException(ErrorFactory.generate(

                    ErrorCode.OPERATION_NOT_FOUND,
                    operations.stream().map(OperationDto::getName).filter(e -> foundNames.contains(e) == false).toList()
                ));
            }
        }

        else {

            throw new EntityExistsException(ErrorFactory.generate(ErrorCode.OPERATION_ALREADY_EXISTS, existingOperations));
        }
    }

    ///..
    @Transactional
    public void deleteOperations(String username, List<Short> ids) throws EntityNotFoundException {

        List<Operation> operations = repository.findAllById(ids);

        if(operations.size() == ids.size()) {

            repository.deleteAllById(ids);

            long now = System.currentTimeMillis();
            List<Audit> audits = new ArrayList<>();
            operations.forEach(o -> audits.addAll(AuditUtils.deleteOperationAudit(o, now, username)));

            auditRepository.saveAll(audits);
        }

        else {

            ids.removeAll(operations.stream().map(Operation::getId).toList());
            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.OPERATION_NOT_FOUND, ids));
        }
    }

    ///
}
