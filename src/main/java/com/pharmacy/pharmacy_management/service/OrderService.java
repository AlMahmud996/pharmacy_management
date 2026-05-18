package com.pharmacy.pharmacy_management.service;

import com.pharmacy.pharmacy_management.model.Order;
import com.pharmacy.pharmacy_management.model.OrderItem;
import com.pharmacy.pharmacy_management.model.Product;
import com.pharmacy.pharmacy_management.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    // Place order
    public String placeOrder(Long customerId, List<OrderItem> items) {

        // Calculate total amount
        double totalAmount = 0;
        for (OrderItem item : items) {

            // Check stock
            if (!productService.isStockAvailable(item.getProductId(),
                    item.getQuantity())) {
                return "Product ID " + item.getProductId()
                        + " is out of stock!";
            }

            // Get product price
            Product product = productService
                    .getProductById(item.getProductId());
            item.setPrice(product.getPrice() * item.getQuantity());
            totalAmount += item.getPrice();
        }

        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        // Save order and get id
        Long orderId = orderRepository.save(order);

        // Save order items & reduce stock
        for (OrderItem item : items) {
            item.setOrderId(orderId);
            orderRepository.saveOrderItem(item);
            productService.reduceStock(item.getProductId(),
                    item.getQuantity());
        }

        return "Order placed successfully! Order ID: " + orderId
                + " | Total: $" + totalAmount;
    }

    // Pay for order
    public String payOrder(Long orderId) {
        int result = orderRepository.updateStatus(orderId, "PAID");
        if (result > 0) {
            return "Payment successful! Order ID: "
                    + orderId + " is now PAID ✅";
        }
        return "Order not found!";
    }

    // Get my orders
    public List<Order> getMyOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}