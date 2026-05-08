package com.example.repository.application;

import com.example.repository.domain.Product;
import com.example.repository.domain.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() { return productRepository.findAll(); }

    public Optional<Product> findById(String id) { return productRepository.findById(id); }

    public List<Product> findByCategory(String category) { return productRepository.findByCategory(category); }

    public List<Product> findLowStock(int threshold) { return productRepository.findLowStock(threshold); }

    @Transactional
    public Product create(String name, String category, BigDecimal price, int stock) {
        return productRepository.save(Product.create(name, category, price, stock));
    }

    @Transactional
    public Product reduceStock(String id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        return productRepository.save(product.withReducedStock(quantity));
    }

    @Transactional
    public void delete(String id) { productRepository.delete(id); }
}
