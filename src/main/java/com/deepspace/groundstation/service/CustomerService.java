package com.deepspace.groundstation.service;

import com.deepspace.groundstation.dto.CustomerRequest;
import com.deepspace.groundstation.dto.CustomerResponse;
import com.deepspace.groundstation.entity.Customer;
import com.deepspace.groundstation.exception.ResourceNotFoundException;
import com.deepspace.groundstation.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer();
        apply(request, customer);
        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findEntity(id);
        apply(request, customer);
        return toResponse(customerRepository.save(customer));
    }

    public void delete(Long id) {
        customerRepository.delete(findEntity(id));
    }

    private Customer findEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " not found"));
    }

    private void apply(CustomerRequest request, Customer customer) {
        customer.setOperatorName(request.operatorName());
        customer.setContactPerson(request.contactPerson());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
        customer.setBillingRatePerGB(request.billingRatePerGB());
        customer.setTrackingRatePerHour(request.trackingRatePerHour());
        customer.setActive(request.active() == null || request.active());
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getOperatorName(), customer.getContactPerson(),
                customer.getEmail(), customer.getPhone(), customer.getAddress(), customer.getBillingRatePerGB(),
                customer.getTrackingRatePerHour(), customer.isActive());
    }
}
