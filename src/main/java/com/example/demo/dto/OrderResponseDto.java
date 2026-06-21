package com.example.demo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private Long storeId;
    private String storeName;
    private LocalDateTime orderDate;
    private String status;
    private String deliveryType;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private List<OrderDetailResponseDto> details;
}
