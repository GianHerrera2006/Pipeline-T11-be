package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseOrderDetailRequestDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal purchasePrice;
}