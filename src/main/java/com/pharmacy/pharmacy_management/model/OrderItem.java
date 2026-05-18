package com.pharmacy.pharmacy_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double price;
}