package io.github.clamentos.grapher.auth.business.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Log;
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilter;

@Service
public class ObservabilityService {

    private final AuditRepository auditRepository;
    private final LogRepository logRepository;
    private final ValidatorService validatorService;

    @Autowired
    public ObservabilityService(AuditRepository auditRepository, LogRepository logRepository, ValidatorService validatorService) {

        this.auditRepository = auditRepository;
        this.logRepository = logRepository;
        this.validatorService = validatorService;
    }

    public List<Audit> getAllAuditsByFilter(AuditSearchFilter searchFilter) {

        validatorService.validateAuditSearchFilter(searchFilter);

        if(searchFilter.getRecordId() != null) {

            return(auditRepository.findAllByRecordId(searchFilter.getRecordId()));
        }

        return(auditRepository.findAllByFilter(

            searchFilter.getTableNames(),
            searchFilter.getAuditActions(),
            searchFilter.getCreatedAtStart(),
            searchFilter.getCreatedAtEnd(),
            searchFilter.getCreatedByNames(),
            PageRequest.of(searchFilter.getPageNumber(), searchFilter.getPageSize())
        ));
    }

    public List<Log> getAllLogsByFilter(LogSearchFilter searchFilter) {

        validatorService.validateLogSearchFilter(searchFilter);

        return(logRepository.findAllByFilter(

            searchFilter.getTimestampStart(),
            searchFilter.getTimestampEnd(),
            searchFilter.getLevels(),
            searchFilter.getThreads(),
            searchFilter.getMessage(),
            searchFilter.getCreatedAtStart(),
            searchFilter.getCreatedAtEnd(),
            PageRequest.of(searchFilter.getPageNumber(), searchFilter.getPageSize())
        ));
    }

    @Transactional
    public void deleteAllAuditsByPeriod(long start, long end) {

        auditRepository.deleteAllByPeriod(start, end);
    }

    @Transactional
    public void deleteAllLogsByPeriod(long start, long end) {

        logRepository.deleteAllByPeriod(start, end);
    }
}
