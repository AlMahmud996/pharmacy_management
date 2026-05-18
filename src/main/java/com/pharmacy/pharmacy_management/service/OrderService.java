package com.pharmacy.pharmacy_management.service;

import com.pharmacy.pharmacy_management.dto.OrderDetailsDTO;
import com.pharmacy.pharmacy_management.dto.OrderItemDTO;
import com.pharmacy.pharmacy_management.model.Order;
import com.pharmacy.pharmacy_management.model.OrderItem;
import com.pharmacy.pharmacy_management.model.Product;
import com.pharmacy.pharmacy_management.repository.OrderRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate; // Added for customer name query

    public OrderService(OrderRepository orderRepository,
                        ProductService productService,
                        NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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

    // NEW METHOD: Get order details with items
    public OrderDetailsDTO getOrderDetails(Long orderId, Long customerId) {
        // 1. Get the order
        Order order = orderRepository.findById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        // 2. Verify this order belongs to the customer
        if (!order.getCustomerId().equals(customerId)) {
            throw new RuntimeException("You can only view your own orders!");
        }

        // 3. Get customer name
        String customerName = getCustomerName(customerId);

        // 4. Get order items with product details
        List<OrderItemDTO> items = orderRepository.getOrderItemsWithDetails(orderId);

        // 5. Build response DTO
        OrderDetailsDTO details = new OrderDetailsDTO();
        details.setOrderId(order.getId());
        details.setCustomerId(order.getCustomerId());
        details.setCustomerName(customerName);
        details.setOrderDate(order.getOrderDate());
        details.setTotalAmount(order.getTotalAmount());
        details.setStatus(order.getStatus());
        details.setItems(items);

        return details;
    }

    // Helper method to get customer name
    private String getCustomerName(Long customerId) {
        String sql = "SELECT username FROM users WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", customerId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            return "Unknown Customer";
        }
    }
}