package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategory(Long id);
    List<CategoryDTO> getCategoryTree();
}