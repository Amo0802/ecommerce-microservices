package com.ecommerce.user_service.mapper;

import com.ecommerce.user_service.dto.UserDTO;
import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserMapper {

    @Mapping(source = "status", target = "status")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserDTO toDTO(User user);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}