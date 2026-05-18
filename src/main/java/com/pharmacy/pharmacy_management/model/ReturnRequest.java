package com.pharmacy.pharmacy_management.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequest {
    private Long id;
    private Long orderId;
    private Long customerId;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String status;
    private LocalDateTime requestDate;
}