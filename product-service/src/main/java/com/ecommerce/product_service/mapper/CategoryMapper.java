package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.CategoryDTO;
import com.ecommerce.product_service.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "children", ignore = true)
    CategoryDTO toDTO(Category category);

    @Mapping(source = "parent.id", target = "parentId")
    CategoryDTO toDTOWithChildren(Category category);
}