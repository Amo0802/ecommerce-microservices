package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductDTO;
import com.ecommerce.product_service.dto.UpdateProductRequest;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.entity.ProductAttribute;
import com.ecommerce.product_service.exception.CategoryNotFoundException;
import com.ecommerce.product_service.exception.DuplicateSkuException;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(CreateProductRequest request) {
        log.info("Creating new product with SKU: {}", request.getSku());

        // Check if SKU already exists
        productRepository.findBySku(request.getSku()).ifPresent(existing -> {
            throw new DuplicateSkuException("Product with SKU " + request.getSku() + " already exists");
        });

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Create product
        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(true)
                .build();

        // Add attributes
        if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
            Set<ProductAttribute> attributes = request.getAttributes().entrySet().stream()
                    .map(entry -> ProductAttribute.builder()
                            .product(product)
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .build())
                    .collect(Collectors.toSet());
            product.setAttributes(attributes);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return productMapper.toDTO(savedProduct);
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Update fields if provided
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        // Update category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        // Update attributes if provided
        if (request.getAttributes() != null) {
            product.getAttributes().clear();
            Set<ProductAttribute> newAttributes = request.getAttributes().entrySet().stream()
                    .map(entry -> ProductAttribute.builder()
                            .product(product)
                            .name(entry.getKey())
                            .value(entry.getValue())
                            .build())
                    .collect(Collectors.toSet());
            product.setAttributes(newAttributes);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());

        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProduct(Long id) {
        log.debug("Fetching product with ID: {}", id);
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toDTO(product);
    }

    @Override
    @Cacheable(value = "products", key = "#sku")
    public ProductDTO getProductBySku(String sku) {
        log.debug("Fetching product with SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return productMapper.toDTO(product);
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Fetching all active products");
        return productRepository.findByActiveTrue(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDTO> searchProducts(Long categoryId, BigDecimal minPrice,
                                           BigDecimal maxPrice, String search, Pageable pageable) {
        log.debug("Searching products with filters - categoryId: {}, minPrice: {}, maxPrice: {}, search: {}",
                categoryId, minPrice, maxPrice, search);
        return productRepository.searchProducts(categoryId, minPrice, maxPrice, search, pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        log.debug("Fetching products by IDs: {}", productIds);
        return productRepository.findByIdIn(productIds).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Soft delete
        product.setActive(false);
        productRepository.save(product);
        log.info("Product soft deleted with ID: {}", id);
    }
}