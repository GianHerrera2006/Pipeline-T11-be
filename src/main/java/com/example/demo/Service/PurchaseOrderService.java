package com.example.demo.Service;

import com.example.demo.Repository.*;
import com.example.demo.dto.*;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrderRequestDto dto) {

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setIssueDate(LocalDate.now());
        purchaseOrder.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        purchaseOrder.setStatus("emitida");
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setStore(store);

        BigDecimal total = BigDecimal.ZERO;
        List<PurchaseOrderDetail> details = new ArrayList<>();

        for (PurchaseOrderDetailRequestDto item : dto.getDetails()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            BigDecimal subtotal = item.getPurchasePrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setPurchaseOrder(purchaseOrder);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPurchasePrice(item.getPurchasePrice());

            details.add(detail);
            total = total.add(subtotal);
        }

        purchaseOrder.setTotalAmount(total);
        purchaseOrder.setDetails(details);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }
}