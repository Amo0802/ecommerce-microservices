package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductDTO;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.entity.ProductAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "attributes", target = "attributes", qualifiedByName = "attributesToMap")
    ProductDTO toDTO(Product product);

    @Named("attributesToMap")
    default Map<String, String> attributesToMap(Set<ProductAttribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }
        return attributes.stream()
                .collect(Collectors.toMap(
                        ProductAttribute::getName,
                        ProductAttribute::getValue
                ));
    }
}