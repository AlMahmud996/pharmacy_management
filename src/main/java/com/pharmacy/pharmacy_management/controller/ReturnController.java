package com.pharmacy.pharmacy_management.controller;

import com.pharmacy.pharmacy_management.model.ReturnRequest;
import com.pharmacy.pharmacy_management.service.ReturnService;
import com.pharmacy.pharmacy_management.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;
    private final UserRepository userRepository;

    public ReturnController(ReturnService returnService,
                            UserRepository userRepository) {
        this.returnService = returnService;
        this.userRepository = userRepository;
    }

    // POST request return (Customer)
    @PostMapping
    public ResponseEntity<String> requestReturn(
            @Valid @RequestBody ReturnRequest returnRequest,
            Authentication authentication) {
        Long customerId = userRepository
                .findByUsername(authentication.getName()).getId();
        returnRequest.setCustomerId(customerId);
        return ResponseEntity.ok(
                returnService.requestReturn(returnRequest));
    }

    // GET all returns (Admin)
    @GetMapping
    public ResponseEntity<List<ReturnRequest>> getAllReturns() {
        return ResponseEntity.ok(returnService.getAllReturns());
    }

    // PUT update return status (Admin)
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateReturnStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(
                returnService.updateReturnStatus(id, status));
    }
}