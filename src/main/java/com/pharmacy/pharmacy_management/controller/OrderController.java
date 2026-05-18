package com.pharmacy.pharmacy_management.controller;

import com.pharmacy.pharmacy_management.model.Order;
import com.pharmacy.pharmacy_management.model.OrderItem;
import com.pharmacy.pharmacy_management.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.pharmacy.pharmacy_management.repository.UserRepository;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService,
                           UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    // POST place order (Customer)
    @PostMapping
    public ResponseEntity<String> placeOrder(
            @RequestBody List<OrderItem> items,
            Authentication authentication) {
        // Get current logged in customer id
        Long customerId = userRepository
                .findByUsername(authentication.getName()).getId();
        return ResponseEntity.ok(
                orderService.placeOrder(customerId, items));
    }

    // POST pay for order (Customer)
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<String> payOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.payOrder(orderId));
    }

    // GET my orders (Customer)
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(
            Authentication authentication) {
        Long customerId = userRepository
                .findByUsername(authentication.getName()).getId();
        return ResponseEntity.ok(
                orderService.getMyOrders(customerId));
    }
}