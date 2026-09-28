package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.ProductRequest;
import com.deepspace.groundstation.dto.ProductResponse;
import com.deepspace.groundstation.entity.Product;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(request, product);
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntity(id);
        apply(request, product);
        return toResponse(productRepository.save(product));
    }

    public void delete(Long id) {
        productRepository.delete(findEntity(id));
    }

    private Product findEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    private void apply(ProductRequest request, Product product) {
        product.setProductCode(request.productCode());
        product.setProductName(request.productName());
        product.setDescription(request.description());
        product.setType(request.type());
        product.setUnit(request.unit());
        product.setUnitPrice(request.unitPrice());
        product.setActive(request.active() == null || request.active());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getProductCode(), product.getProductName(),
                product.getDescription(), product.getType(), product.getUnit(), product.getUnitPrice(), product.isActive());
    }
}
