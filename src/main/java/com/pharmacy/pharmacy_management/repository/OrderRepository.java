package com.pharmacy.pharmacy_management.repository;

import com.pharmacy.pharmacy_management.dto.OrderItemDTO;
import com.pharmacy.pharmacy_management.model.Order;
import com.pharmacy.pharmacy_management.model.OrderItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class OrderRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // Create order
    public Long save(Order order) {
        String sql = "INSERT INTO orders (customer_id, total_amount, status) " +
                "VALUES (:customerId, :totalAmount, :status)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("customerId", order.getCustomerId());
        params.addValue("totalAmount", order.getTotalAmount());
        params.addValue("status", "PENDING");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().longValue();
    }

    // Save order items
    public void saveOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                "VALUES (:orderId, :productId, :quantity, :price)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderId", item.getOrderId());
        params.addValue("productId", item.getProductId());
        params.addValue("quantity", item.getQuantity());
        params.addValue("price", item.getPrice());
        namedParameterJdbcTemplate.update(sql, params);
    }

    // Get orders by customer
    public List<Order> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = :customerId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("customerId", customerId);
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setCustomerId(rs.getLong("customer_id"));
            order.setOrderDate(rs.getTimestamp("order_date") != null ?
                    rs.getTimestamp("order_date").toLocalDateTime() : null);
            order.setTotalAmount(rs.getDouble("total_amount"));
            order.setStatus(rs.getString("status"));
            return order;
        });
    }

    // Update order status
    public int updateStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET status = :status WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", status);
        params.addValue("id", orderId);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    // NEW METHOD 1: Find order by ID
    public Order findById(Long orderId) {
        String sql = "SELECT * FROM orders WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", orderId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setCustomerId(rs.getLong("customer_id"));
                order.setOrderDate(rs.getTimestamp("order_date") != null ?
                        rs.getTimestamp("order_date").toLocalDateTime() : null);
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                return order;
            });
        } catch (Exception e) {
            return null;
        }
    }

    // NEW METHOD 2: Get order items with product details
    public List<OrderItemDTO> getOrderItemsWithDetails(Long orderId) {
        String sql = "SELECT oi.product_id, oi.quantity, oi.price, " +
                "p.name as product_name " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "WHERE oi.order_id = :orderId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderId", orderId);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            OrderItemDTO item = new OrderItemDTO();
            item.setProductId(rs.getLong("product_id"));
            item.setProductName(rs.getString("product_name"));
            item.setQuantity(rs.getInt("quantity"));
            item.setPrice(rs.getDouble("price"));
            item.setSubtotal(rs.getDouble("price") * rs.getInt("quantity"));
            return item;
        });
    }
}