package io.github.clamentos.grapher.auth.business.mappers;

///
import io.github.clamentos.grapher.auth.persistence.entities.Operation;

///..
import io.github.clamentos.grapher.auth.web.dtos.OperationDto;

///.
import java.util.List;

///.
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

///
@Mapper(componentModel = "spring")

///
public interface OperationMapper {

    ///
    @Mapping(target = "userOperations", ignore = true)
    @Mapping(target = "instantAudit", ignore = true)
    Operation mapIntoEntity(OperationDto operation);

    ///..
    List<Operation> mapIntoEntities(List<OperationDto> operations);

    ///..
    @Mapping(target = "createdAt", source = "instantAudit.createdAt")
    @Mapping(target = "updatedAt", source = "instantAudit.updatedAt")
    @Mapping(target = "createdBy", source = "instantAudit.createdBy")
    @Mapping(target = "updatedBy", source = "instantAudit.updatedBy")
    OperationDto mapIntoDto(Operation operation);

    ///..
    List<OperationDto> mapIntoDtos(List<Operation> operations);

    ///
}
