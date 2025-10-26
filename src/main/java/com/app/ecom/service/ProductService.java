package com.app.ecom.service;

import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.model.Product;
import com.app.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing Product entities.
 *
 * <p>This class contains all business logic related to products — including
 * creating, updating, deleting (soft delete), fetching, and searching products.
 * It interacts directly with the {@link ProductRepository} and transforms
 * data between Entity and DTO layers to keep the controller lightweight.</p>
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    /** Injected via Lombok’s @RequiredArgsConstructor */
    private final ProductRepository productRepository;

    // ------------------------------------------------------------------------
    // CREATE PRODUCT
    // ------------------------------------------------------------------------

    /**
     * Creates a new product using data from ProductRequest DTO.
     * Maps the request to an entity, persists it in the database,
     * and returns a ProductResponse DTO.
     *
     * @param productRequest DTO containing product details
     * @return ProductResponse DTO representing the saved product
     */
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Step 1: Convert request → entity
        Product product = new Product();
        updateProductFromRequest(product, productRequest);

        // Step 2: Save to repository
        Product savedProduct = productRepository.save(product);

        // Step 3: Convert entity → response
        return mapToProductResponse(savedProduct);
    }

    // ------------------------------------------------------------------------
    // UPDATE PRODUCT
    // ------------------------------------------------------------------------

    /**
     * Updates an existing product by ID if found.
     * Uses Optional to handle cases where the product doesn’t exist.
     *
     * @param id              Product ID
     * @param productRequest  Updated product data
     * @return Optional<ProductResponse> — empty if product not found
     */
    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Update fields from request
                    updateProductFromRequest(existingProduct, productRequest);

                    // Save changes
                    Product savedProduct = productRepository.save(existingProduct);

                    // Convert and return response
                    return mapToProductResponse(savedProduct);
                });
    }

    // ------------------------------------------------------------------------
    // HELPER METHODS — DTO MAPPERS
    // ------------------------------------------------------------------------

    /**
     * Maps Product entity → ProductResponse DTO.
     * This is used for API responses to ensure frontend doesn’t directly depend on JPA entities.
     */
    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setActive(product.isActive());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setStockQuantity(product.getStockQuantity());
        return response;
    }

    /**
     * Maps ProductRequest DTO → Product entity.
     * This method avoids duplication and is reused for both create and update operations.
     * Note: The ID is excluded because it is auto-generated.
     */
    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setActive(request.isActive());       // ensure `active` flag is present in ProductRequest
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setStockQuantity(request.getStockQuantity());
    }

    // ------------------------------------------------------------------------
    // READ / GET OPERATIONS
    // ------------------------------------------------------------------------

    /**
     * Retrieves all products that are currently active.
     * Uses a custom query findByActiveTrue() for soft-deleted products.
     *
     * @return List of ProductResponse DTOs
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------
    // DELETE (SOFT DELETE)
    // ------------------------------------------------------------------------

    /**
     * Performs a soft delete by setting the product’s active flag to false.
     * This ensures data is not permanently deleted, which helps preserve history.
     *
     * @param id Product ID to be deactivated
     * @return true if successfully deactivated, false if product not found
     */
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);           // mark as inactive instead of deleting
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    // ------------------------------------------------------------------------
    // SEARCH PRODUCTS
    // ------------------------------------------------------------------------

    /**
     * Searches products by keyword.
     * This likely uses a custom JPQL/Native query defined in ProductRepository.
     *
     * @param keyword text to search for (in name/description/category)
     * @return List of matching ProductResponse DTOs
     */
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
}
