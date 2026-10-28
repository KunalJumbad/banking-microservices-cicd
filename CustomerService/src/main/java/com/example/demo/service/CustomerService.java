package com.example.demo.service;

import com.example.demo.dto.CustomerRequest;
import com.example.demo.entity.Customer;
import com.example.demo.entity.SavingsDetails;
import java.util.List;

public interface CustomerService {

    Customer createCustomer(CustomerRequest request);

    List<Customer> getAllCustomers();

    Customer getCustomerById(Long customerId);

    Customer getProfile();
    
    List<SavingsDetails> getSavingsSummary(Long customerId);

    String getSavingsServicePort();
}
