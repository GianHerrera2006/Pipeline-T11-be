package com.example.demo.Service;

import com.example.demo.Repository.InventoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.StoreRepository;
import com.example.demo.dto.InventoryRequestDto;
import com.example.demo.model.Inventory;
import com.example.demo.model.Product;
import com.example.demo.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Transactional
    public Inventory registerStock(InventoryRequestDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        Optional<Inventory> existingInventory = inventoryRepository.findByProductIdAndStoreId(dto.getProductId(), dto.getStoreId());

        Inventory inventory;
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            inventory.setStock(dto.getStock());
        } else {
            inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setStore(store);
            inventory.setStock(dto.getStock());
        }

        if (dto.getMinStock() != null) {
            inventory.setMinStock(dto.getMinStock());
        }

        return inventoryRepository.save(inventory);
    }

    public List<Inventory> findByStore(Long storeId) {
        return inventoryRepository.findByStoreId(storeId);
    }
    
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }
}
