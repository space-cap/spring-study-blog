package com.develead.smile.service;

import com.develead.smile.domain.Customer;
import com.develead.smile.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Integer id) {
        // 실제로는 deleted 플래그를 'Y'로 업데이트 (Soft Delete)
        customerRepository.findById(id).ifPresent(customer -> {
            customer.setDeleted("Y");
            customerRepository.save(customer);
        });
    }
}
