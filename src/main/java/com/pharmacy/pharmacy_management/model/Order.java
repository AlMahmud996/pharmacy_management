package com.pharmacy.pharmacy_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
}