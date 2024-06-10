package io.github.clamentos.grapher.auth.business.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.clamentos.grapher.auth.persistence.entities.User;
import io.github.clamentos.grapher.auth.web.dtos.UserDetails;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "operations", ignore = true)
    User map(UserDetails userDetails);
}
