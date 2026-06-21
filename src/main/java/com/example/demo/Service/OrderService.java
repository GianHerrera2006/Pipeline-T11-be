package com.example.demo.Service;

import com.example.demo.Repository.*;
import com.example.demo.dto.*;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        // 1. Validar Cliente
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Validar Tienda
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));

        // 3. Validar Usuario (si aplica)
        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario/Empleado no encontrado"));
        }

        // 4. Crear Cabecera de Orden
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        order.setStore(store);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("pagado"); // Sincronizado con data.sql
        order.setDeliveryType(dto.getDeliveryType().toLowerCase()); // delivery o recogida
        order.setDeliveryAddress(dto.getDeliveryAddress());

        BigDecimal subtotal = BigDecimal.ZERO;

        // 5. Procesar Detalles y Stock
        for (OrderDetailRequestDto detailDto : dto.getDetails()) {
            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detailDto.getProductId()));

            // Verificar Stock
            Inventory inventory = inventoryRepository.findByProductIdAndStoreId(product.getId(), store.getId())
                    .orElseThrow(() -> new RuntimeException("No hay inventario para el producto en esta tienda"));

            if (inventory.getStock() < detailDto.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            // Restar Stock
            inventory.setStock(inventory.getStock() - detailDto.getQuantity());
            inventoryRepository.save(inventory);

            // Crear Detalle
            OrderDetail detail = new OrderDetail();
            detail.setProduct(product);
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(product.getBasePrice());
            BigDecimal itemSubtotal = product.getBasePrice().multiply(BigDecimal.valueOf(detailDto.getQuantity()));
            detail.setSubtotal(itemSubtotal);
            
            order.addDetail(detail);
            subtotal = subtotal.add(itemSubtotal);
        }

        // 6. Cálculos de Totales (IGV 18%)
        BigDecimal taxRate = new BigDecimal("0.18");
        BigDecimal tax = subtotal.multiply(taxRate);
        BigDecimal total = subtotal.add(tax);

        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);

        // 7. Guardar Orden (Cascade guarda detalles)
        Order savedOrder = orderRepository.save(order);

        // 8. Registrar Pago
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setAmount(total);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("pagado"); // Sincronizado con data.sql (CHK_payment_status)
        paymentRepository.save(payment);

        return mapToResponseDto(savedOrder);
    }

    public List<OrderResponseDto> findAll() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        OrderResponseDto response = new OrderResponseDto();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        response.setStoreId(order.getStore().getId());
        response.setStoreName(order.getStore().getName());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus());
        response.setDeliveryType(order.getDeliveryType());
        response.setSubtotal(order.getSubtotal());
        response.setTax(order.getTax());
        response.setTotal(order.getTotal());

        List<OrderDetailResponseDto> details = order.getDetails().stream().map(d -> {
            OrderDetailResponseDto dDto = new OrderDetailResponseDto();
            dDto.setProductId(d.getProduct().getId());
            dDto.setProductName(d.getProduct().getName());
            dDto.setQuantity(d.getQuantity());
            dDto.setUnitPrice(d.getUnitPrice());
            dDto.setSubtotal(d.getSubtotal());
            return dDto;
        }).collect(Collectors.toList());

        response.setDetails(details);
        return response;
    }
}
