package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.entity.Customer;
import com.example.demo.entity.SavingsDetails;
import com.example.demo.service.CustomerService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /api/v1/customers - retrieves all customers for dashboard and customer list
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // GET /api/v1/customers/profile - retains existing profile stand-in
    @GetMapping("/profile")
    public ResponseEntity<Customer> getProfile() {
        return ResponseEntity.ok(customerService.getProfile());
    }

    // POST /api/v1/customers
    @PostMapping
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody CustomerRequest request) {

        Customer savedCustomer =
                customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedCustomer);
    }

    // GET /api/v1/customers/{customerid}
    @GetMapping("/{customerid}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable("customerid") Long customerId) {

        return ResponseEntity.ok(
                customerService.getCustomerById(customerId)
        );
    }

    // GET /api/v1/customers/savings/{customerid}
    @GetMapping("/savings/{customerid}")
    public ResponseEntity<List<SavingsDetails>> getSavingsSummary(
            @PathVariable("customerid") Long customerId) {

        List<SavingsDetails> details =
                customerService.getSavingsSummary(customerId);

        return ResponseEntity.ok(details);
    }
    
    @GetMapping("/savings/port")
    public ResponseEntity<String> getSavingsServicePort() {
        return ResponseEntity.ok(
            customerService.getSavingsServicePort()
        );
    }

    // GET /api/v1/customers/loans/{customerid}
    @GetMapping("/loans/{customerid}")
    public Object getLoans(@PathVariable Long customerid) {
        return null;
    }
}
