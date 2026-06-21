package com.example.demo.Repository;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL AND c.isActive = true")
    List<Customer> findAllActive();

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByDocNumber(String docNumber);

    Optional<Customer> findByEmail(String email);
}