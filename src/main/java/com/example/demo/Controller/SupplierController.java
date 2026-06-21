package com.example.demo.Controller;

import com.example.demo.model.Supplier;
import com.example.demo.Service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(path = {"/api/suppliers"})
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @GetMapping
    public List<Supplier> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/activos")
    public List<Supplier> listarActivos() {
        return service.listarActivos();
    }

    @GetMapping("/{id}")
    public Supplier obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Supplier crear(@Valid @RequestBody Supplier supplier) {
        return service.crear(supplier);
    }

    @PutMapping("/{id}")
    public Supplier actualizar(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        return service.actualizar(id, supplier);
    }

    @PatchMapping("/{id}/eliminar")
    public ResponseEntity<Void> eliminarLogico(@PathVariable Long id) {
        service.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        service.restaurar(id);
        return ResponseEntity.noContent().build();
    }
}