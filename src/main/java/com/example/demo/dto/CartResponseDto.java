package com.example.demo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    private String cartToken;
    private List<CartItemResponseDto> items;
    private BigDecimal total;
}
