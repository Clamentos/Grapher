package io.github.clamentos.grapher.auth.business.mappers;

///
import io.github.clamentos.grapher.auth.persistence.entities.Audit;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditDto;

///.
import java.util.List;

///.
import org.mapstruct.Mapper;

///
@Mapper(componentModel = "spring")

///
public interface AuditMapper {

    ///
    AuditDto mapIntoDto(Audit audit);

    ///..
    List<AuditDto> mapIntoDtos(List<Audit> audits);

    ///
}
