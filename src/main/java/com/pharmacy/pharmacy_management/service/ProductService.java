package com.pharmacy.pharmacy_management.service;

import com.pharmacy.pharmacy_management.model.Product;
import com.pharmacy.pharmacy_management.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get product by id
    public Product getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add new product (Admin)
    public String addProduct(Product product) {
        int result = productRepository.save(product);
        if (result > 0) {
            return "Product added successfully!";
        }
        return "Failed to add product!";
    }

    // Update price (Admin)
    public String updatePrice(Long id, Double price) {
        if (price <= 0) {
            return "Price must be greater than 0!";
        }
        int result = productRepository.updatePrice(id, price);
        if (result > 0) {
            return "Price updated successfully!";
        }
        return "Product not found!";
    }

    // Check stock availability
    public boolean isStockAvailable(Long id, Integer quantity) {
        Product product = productRepository.findById(id);
        return product.getStock() >= quantity;
    }

    // Reduce stock after order
    public void reduceStock(Long id, Integer quantity) {
        productRepository.updateStock(id, quantity);
    }
}