package com.example.demo.Repository;

import com.example.demo.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByIsActive(Boolean isActive);

    Optional<Supplier> findBySupplierCode(String supplierCode);

    Optional<Supplier> findByDocNumber(String docNumber);

    Optional<Supplier> findByEmail(String email);
}