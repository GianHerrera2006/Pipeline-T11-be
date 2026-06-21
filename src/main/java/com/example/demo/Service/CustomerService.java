package com.example.demo.Service;

import com.example.demo.Repository.CustomerRepository;
import com.example.demo.dto.CustomerRegistrationDto;
import com.example.demo.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer registerCustomer(CustomerRegistrationDto dto) {
        if (customerRepository.findByCustomerCode(dto.getCustomerCode()).isPresent()) {
            throw new RuntimeException("Customer code already exists");
        }
        if (customerRepository.findByDocNumber(dto.getDocNumber()).isPresent()) {
            throw new RuntimeException("Documento ya registrado");
        }
        if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }

        Customer customer = new Customer();
        customer.setCustomerCode(dto.getCustomerCode());
        customer.setDocType(dto.getDocType());
        customer.setDocNumber(dto.getDocNumber());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setUbigeoId(dto.getUbigeoId());
        customer.setPassword(dto.getPassword());
        customer.setTotalVisits(dto.getTotalVisits() != null ? dto.getTotalVisits() : 0);
        customer.setLastPurchaseDate(dto.getLastPurchaseDate());
        customer.setCustomerSince(dto.getCustomerSince() != null ? dto.getCustomerSince() : LocalDate.now());
        customer.setNotes(dto.getNotes());
        customer.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        customer.setDeletedAt(null);
        customer.setRestoredAt(null);

        return customerRepository.save(customer);
    }

    public List<Customer> findAllActive() {
        return customerRepository.findAllActive();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerRegistrationDto dto) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        Customer customer = optionalCustomer.get();

        customer.setCustomerCode(dto.getCustomerCode());
        customer.setDocType(dto.getDocType());
        customer.setDocNumber(dto.getDocNumber());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setUbigeoId(dto.getUbigeoId());
        customer.setPassword(dto.getPassword());
        customer.setTotalVisits(dto.getTotalVisits() != null ? dto.getTotalVisits() : customer.getTotalVisits());
        customer.setLastPurchaseDate(dto.getLastPurchaseDate());
        customer.setCustomerSince(dto.getCustomerSince() != null ? dto.getCustomerSince() : customer.getCustomerSince());
        customer.setNotes(dto.getNotes());
        if (dto.getIsActive() != null) {
            customer.setIsActive(dto.getIsActive());
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteLogical(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        Customer customer = optionalCustomer.get();
        customer.setIsActive(false);
        customer.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Transactional
    public void restore(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        Customer customer = optionalCustomer.get();
        customer.setIsActive(true);
        customer.setDeletedAt(null);
        customer.setRestoredAt(LocalDateTime.now());
        customerRepository.save(customer);
    }
}