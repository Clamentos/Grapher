package io.github.clamentos.grapher.auth.business.mappers;

///
import io.github.clamentos.grapher.auth.persistence.entities.ApiPermission;

///..
import io.github.clamentos.grapher.auth.web.dtos.ApiPermissionDto;

///.
import java.util.List;

///.
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

///
@Mapper(componentModel = "spring", uses = { OperationMapper.class })

///
public interface ApiPermissionMapper {

    ///
    @Mapping(target = "optional", source = "isOptional")
    @Mapping(target = "instantAudit", ignore = true)
    @Mapping(target = "operation", ignore = true)
    ApiPermission mapIntoEntity(ApiPermissionDto permission);

    ///..
    List<ApiPermission> mapIntoEntities(List<ApiPermissionDto> permissions);

    ///..
    @Mapping(target = "isOptional", source = "optional")
    @Mapping(target = "createdAt", source = "instantAudit.createdAt")
    @Mapping(target = "updatedAt", source = "instantAudit.updatedAt")
    @Mapping(target = "createdBy", source = "instantAudit.createdBy")
    @Mapping(target = "updatedBy", source = "instantAudit.updatedBy")
    ApiPermissionDto mapIntoDto(ApiPermission permission);

    ///..
    List<ApiPermissionDto> mapIntoDtos(List<ApiPermission> permissions);

    ///
}
