package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.CreateProductRequest;
import com.ecommerce.product_service.dto.ProductDTO;
import com.ecommerce.product_service.dto.UpdateProductRequest;
import com.ecommerce.product_service.service.ProductService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Timed(value = "product.create", description = "Time taken to create product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        log.info("REST request to create product: {}", request.getSku());
        ProductDTO product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @Timed(value = "product.update", description = "Time taken to update product")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateProductRequest request) {
        log.info("REST request to update product: {}", id);
        ProductDTO product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    @Timed(value = "product.get", description = "Time taken to get product")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        log.debug("REST request to get product: {}", id);
        ProductDTO product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/sku/{sku}")
    @Timed(value = "product.getBySku", description = "Time taken to get product by SKU")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        log.debug("REST request to get product by SKU: {}", sku);
        ProductDTO product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Timed(value = "product.getAll", description = "Time taken to get all products")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("REST request to get all products");
        Page<ProductDTO> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Timed(value = "product.search", description = "Time taken to search products")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("REST request to search products");
        Page<ProductDTO> products = productService.searchProducts(categoryId, minPrice, maxPrice, search, pageable);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/batch")
    @Timed(value = "product.getBatch", description = "Time taken to get products by IDs")
    public ResponseEntity<List<ProductDTO>> getProductsByIds(@RequestBody List<Long> productIds) {
        log.debug("REST request to get products by IDs: {}", productIds);
        List<ProductDTO> products = productService.getProductsByIds(productIds);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    @Timed(value = "product.delete", description = "Time taken to delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("REST request to delete product: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}