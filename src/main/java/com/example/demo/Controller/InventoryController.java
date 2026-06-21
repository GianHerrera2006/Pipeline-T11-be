package com.example.demo.Controller;

import com.example.demo.Service.InventoryService;
import com.example.demo.dto.InventoryRequestDto;
import com.example.demo.model.Inventory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/register")
    public ResponseEntity<Inventory> registerStock(@Valid @RequestBody InventoryRequestDto dto) {
        return ResponseEntity.ok(inventoryService.registerStock(dto));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Inventory>> getByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.findByStore(storeId));
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAll() {
        return ResponseEntity.ok(inventoryService.findAll());
    }
}
