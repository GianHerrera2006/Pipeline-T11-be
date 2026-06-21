package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseOrderRequestDto {
    private Long supplierId;
    private Long storeId;
    private LocalDate expectedDeliveryDate;
    private List<PurchaseOrderDetailRequestDto> details;
}