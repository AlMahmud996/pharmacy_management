package com.pharmacy.pharmacy_management.service;

import com.pharmacy.pharmacy_management.model.ReturnRequest;
import com.pharmacy.pharmacy_management.repository.ReturnRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final OrderService orderService;

    // Valid return reasons
    private static final List<String> VALID_REASONS = List.of(
            "wrong medicine delivered",
            "damaged product",
            "expired medicine",
            "allergic reaction",
            "wrong dosage"
    );

    public ReturnService(ReturnRepository returnRepository,
                         OrderService orderService) {
        this.returnRepository = returnRepository;
        this.orderService = orderService;
    }

    // Request return (Customer)
    public String requestReturn(ReturnRequest returnRequest) {

        // Validate reason
        boolean validReason = VALID_REASONS.stream()
                .anyMatch(r -> returnRequest.getReason()
                        .toLowerCase().contains(r));

        if (!validReason) {
            return "Invalid return reason! Valid reasons are: "
                    + VALID_REASONS;
        }

        int result = returnRepository.save(returnRequest);
        if (result > 0) {
            return "Return request submitted successfully!"
                    + " We will review it shortly.";
        }
        return "Failed to submit return request!";
    }

    // Get all returns (Admin)
    public List<ReturnRequest> getAllReturns() {
        return returnRepository.findAll();
    }

    // Accept or Reject return (Admin)
    public String updateReturnStatus(Long id, String status) {

        // Validate status
        if (!status.equals("ACCEPTED") && !status.equals("REJECTED")) {
            return "Status must be ACCEPTED or REJECTED!";
        }

        int result = returnRepository.updateStatus(id, status);
        if (result > 0) {
            // Notify customer
            String notification = notifyCustomer(id, status);
            return "Return request " + status + "! " + notification;
        }
        return "Return request not found!";
    }

    // Notify customer after return decision
    private String notifyCustomer(Long returnId, String status) {
        if (status.equals("ACCEPTED")) {
            return "Customer has been notified:"
                    + " Your return request #" + returnId
                    + " has been ACCEPTED ✅"
                    + " Refund will be processed shortly.";
        } else {
            return "Customer has been notified:"
                    + " Your return request #" + returnId
                    + " has been REJECTED ❌"
                    + " Please contact support.";
        }
    }
}