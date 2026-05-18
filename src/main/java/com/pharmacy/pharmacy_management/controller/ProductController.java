package com.pharmacy.pharmacy_management.controller;

import com.pharmacy.pharmacy_management.model.Product;
import com.pharmacy.pharmacy_management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products (Admin + Customer)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET product by id (Admin + Customer)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // POST add product (Admin only)
    @PostMapping
    public ResponseEntity<String> addProduct(
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.addProduct(product));
    }

    // PUT update price (Admin only)
    @PutMapping("/{id}/price")
    public ResponseEntity<String> updatePrice(
            @PathVariable Long id,
            @RequestParam Double price) {
        return ResponseEntity.ok(productService.updatePrice(id, price));
    }
}