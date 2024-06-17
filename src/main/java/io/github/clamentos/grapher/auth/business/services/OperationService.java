package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.business.mappers.OperationMapper;

///..
import io.github.clamentos.grapher.auth.exceptions.ValidationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.InstantAudit;
import io.github.clamentos.grapher.auth.persistence.entities.Operation;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.OperationRepository;

///..
import io.github.clamentos.grapher.auth.utility.AuditUtils;
import io.github.clamentos.grapher.auth.utility.ErrorCode;
import io.github.clamentos.grapher.auth.utility.ErrorFactory;
import io.github.clamentos.grapher.auth.utility.Validator;

///..
import io.github.clamentos.grapher.auth.web.dtos.OperationDto;

///.
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

///..
import jakarta.transaction.Transactional;

///.
import java.util.ArrayList;
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Service;

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
    public void createOperations(String username, List<String> operations) throws EntityExistsException, ValidationException {

        List<String> alreadyExists = repository.listExists(operations);

        if(alreadyExists.size() == 0) {

            List<Operation> entities = new ArrayList<>();
            long now = System.currentTimeMillis();

            for(String operation : operations) {

                Validator.requireFilled(operation, "operation");
                entities.add(new Operation((short)0, operation, null, new InstantAudit(0, now, now, username, username)));
            }

            entities = repository.saveAll(entities);
            List<Audit> audits = new ArrayList<>();

            for(Operation entity : entities) {

                audits.addAll(AuditUtils.insertOperationAudit(entity));
            }

            auditRepository.saveAll(audits);
        }

        else {

            throw new EntityExistsException(ErrorFactory.generate(ErrorCode.ENTITY_ALREADY_EXISTS, alreadyExists));
        }
    }

    ///..
    public List<OperationDto> readOperations() {

        return(mapper.mapIntoDtos(repository.findAll()));
    }

    ///..
    @Transactional
    public void updateOperation(String username, OperationDto operation) throws EntityNotFoundException, ValidationException {

        Validator.validateAuditedObject(operation);
        Validator.requireNotNull(operation.getId(), "id");
        Validator.requireFilled(operation.getName(), "name");

        Operation entity = repository.findById(operation.getId()).get();

        if(entity != null) {

            entity.setName(operation.getName());
            entity.getInstantAudit().setUpdatedAt(System.currentTimeMillis());
            entity.getInstantAudit().setUpdatedBy(username);

            repository.save(entity);
            auditRepository.saveAll(AuditUtils.updateOperationAudit(entity));
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.ENTITY_NOT_FOUND, operation.getId()));
        }
    }

    ///..
    @Transactional
    public void deleteOperation(String username, short id) throws EntityNotFoundException {

        Operation entity = repository.findById(id).get();

        if(entity != null) {

            repository.delete(entity);
            auditRepository.saveAll(AuditUtils.deleteOperationAudit(entity, System.currentTimeMillis(), username));
        }

        else {

            throw new EntityNotFoundException(ErrorFactory.generate(ErrorCode.ENTITY_NOT_FOUND, id));
        }
    }

    ///
}
