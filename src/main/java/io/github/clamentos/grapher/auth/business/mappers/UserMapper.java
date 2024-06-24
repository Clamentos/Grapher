package io.github.clamentos.grapher.auth.business.mappers;

///
import io.github.clamentos.grapher.auth.persistence.entities.User;

///..
import io.github.clamentos.grapher.auth.web.dtos.UserDto;

///.
import java.util.List;

///.
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

///
@Mapper(componentModel = "spring", uses = { OperationMapper.class })

///
public interface UserMapper {

    ///
    @Mapping(target = "instantAudit", ignore = true)
    @Mapping(target = "operations", ignore = true)
    User mapIntoEntity(UserDto userDetails);

    ///..
    @Mapping(target = "operations", ignore = true)
    @Mapping(target = "createdAt", source = "instantAudit.createdAt")
    @Mapping(target = "updatedAt", source = "instantAudit.updatedAt")
    @Mapping(target = "createdBy", source = "instantAudit.createdBy")
    @Mapping(target = "updatedBy", source = "instantAudit.updatedBy")
    UserDto mapIntoDto(User user);

    ///..
    List<UserDto> mapIntoDtos(List<User> users);

    ///
}
