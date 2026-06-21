package com.example.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;

    @NotNull(message = "El ID de la tienda es obligatorio")
    private Long storeId;

    private Long userId; // Opcional, si un empleado procesa la compra

    @NotBlank(message = "El tipo de entrega es obligatorio")
    private String deliveryType; // STORE_PICKUP, HOME_DELIVERY

    private String deliveryAddress;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod; // CASH, CREDIT_CARD, DEBIT_CARD, QUOTAS

    private Integer installments; // Número de cuotas si aplica

    @NotEmpty(message = "La orden debe tener al menos un detalle")
    private List<OrderDetailRequestDto> details;
}
