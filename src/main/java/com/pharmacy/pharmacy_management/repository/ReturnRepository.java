package com.pharmacy.pharmacy_management.repository;

import com.pharmacy.pharmacy_management.model.ReturnRequest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ReturnRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReturnRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // Create return request
    public int save(ReturnRequest returnRequest) {
        String sql = "INSERT INTO return_requests (order_id, customer_id, reason, status) " +
                "VALUES (:orderId, :customerId, :reason, :status)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orderId", returnRequest.getOrderId());
        params.addValue("customerId", returnRequest.getCustomerId());
        params.addValue("reason", returnRequest.getReason());
        params.addValue("status", "PENDING");
        return namedParameterJdbcTemplate.update(sql, params);
    }

    // Get all return requests (Admin)
    public List<ReturnRequest> findAll() {
        String sql = "SELECT * FROM return_requests";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
            ReturnRequest r = new ReturnRequest();
            r.setId(rs.getLong("id"));
            r.setOrderId(rs.getLong("order_id"));
            r.setCustomerId(rs.getLong("customer_id"));
            r.setReason(rs.getString("reason"));
            r.setStatus(rs.getString("status"));
            return r;
        });
    }

    // Update return status (Admin)
    public int updateStatus(Long id, String status) {
        String sql = "UPDATE return_requests SET status = :status WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", status);
        params.addValue("id", id);
        return namedParameterJdbcTemplate.update(sql, params);
    }
}