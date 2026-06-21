package com.example.demo.Service;

import com.example.demo.model.Supplier;
import com.example.demo.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository repository;

    public List<Supplier> listarTodos() {
        return repository.findAll();
    }

    public List<Supplier> listarActivos() {
        return repository.findByIsActive(true);
    }

    public Supplier buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    @Transactional
    public Supplier crear(Supplier supplier) {
        supplier.setIsActive(true);
        supplier.setDeletedAt(null);
        supplier.setRestoredAt(null);
        return repository.save(supplier);
    }

    @Transactional
    public Supplier actualizar(Long id, Supplier supplierActualizado) {
        Supplier supplier = buscarPorId(id);

        supplier.setSupplierCode(supplierActualizado.getSupplierCode());
        supplier.setBusinessName(supplierActualizado.getBusinessName());
        supplier.setDocType(supplierActualizado.getDocType());
        supplier.setDocNumber(supplierActualizado.getDocNumber());
        supplier.setPhone(supplierActualizado.getPhone());
        supplier.setEmail(supplierActualizado.getEmail());
        supplier.setAddress(supplierActualizado.getAddress());
        supplier.setUbigeoId(supplierActualizado.getUbigeoId());
        supplier.setContactPerson(supplierActualizado.getContactPerson());
        if (supplierActualizado.getIsActive() != null) {
            supplier.setIsActive(supplierActualizado.getIsActive());
        }
        supplier.setUpdatedAt(LocalDateTime.now());

        return repository.save(supplier);
    }

    @Transactional
    public void eliminarLogico(Long id) {
        Supplier supplier = buscarPorId(id);
        supplier.setIsActive(false);
        supplier.setDeletedAt(LocalDateTime.now());
        repository.save(supplier);
    }

    @Transactional
    public void restaurar(Long id) {
        Supplier supplier = buscarPorId(id);
        supplier.setIsActive(true);
        supplier.setDeletedAt(null);
        supplier.setRestoredAt(LocalDateTime.now());
        repository.save(supplier);
    }
}