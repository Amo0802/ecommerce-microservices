package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductDTO;
import com.ecommerce.product_service.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductDTO createProduct(CreateProductRequest request);

    ProductDTO updateProduct(Long id, UpdateProductRequest request);

    ProductDTO getProduct(Long id);

    ProductDTO getProductBySku(String sku);

    Page<ProductDTO> getAllProducts(Pageable pageable);

    Page<ProductDTO> searchProducts(Long categoryId, BigDecimal minPrice,
                                    BigDecimal maxPrice, String search, Pageable pageable);

    List<ProductDTO> getProductsByIds(List<Long> productIds);

    void deleteProduct(Long id);
}