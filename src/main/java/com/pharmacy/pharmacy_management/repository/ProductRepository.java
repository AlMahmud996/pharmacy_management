package com.pharmacy.pharmacy_management.repository;

import com.pharmacy.pharmacy_management.model.Product;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProductRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProductRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // Get all products
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setDescription(rs.getString("description"));
            product.setPrice(rs.getDouble("price"));
            product.setStock(rs.getInt("stock"));
            product.setCategory(rs.getString("category"));
            return product;
        });
    }

    // Get product by id
    public Product findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setName(rs.getString("name"));
            product.setDescription(rs.getString("description"));
            product.setPrice(rs.getDouble("price"));
            product.setStock(rs.getInt("stock"));
            product.setCategory(rs.getString("category"));
            return product;
        });
    }

    // Insert product (Admin only)
    public int save(Product product) {
        String sql = "INSERT INTO products (name, description, price, stock, category) " +
                "VALUES (:name, :description, :price, :stock, :category)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", product.getName());
        params.addValue("description", product.getDescription());
        params.addValue("price", product.getPrice());
        params.addValue("stock", product.getStock());
        params.addValue("category", product.getCategory());
        return namedParameterJdbcTemplate.update(sql, params);
    }

    // Update price (Admin only)
    public int updatePrice(Long id, Double price) {
        String sql = "UPDATE products SET price = :price WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("price", price);
        params.addValue("id", id);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    // Update stock
    public int updateStock(Long id, Integer stock) {
        String sql = "UPDATE products SET stock = stock - :stock WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("stock", stock);
        params.addValue("id", id);
        return namedParameterJdbcTemplate.update(sql, params);
    }
}