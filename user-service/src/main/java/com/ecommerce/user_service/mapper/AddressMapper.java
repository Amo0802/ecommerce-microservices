package com.ecommerce.user_service.mapper;

import com.ecommerce.user_service.dto.AddressDTO;
import com.ecommerce.user_service.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "type", target = "type")
    AddressDTO toDTO(Address address);
}