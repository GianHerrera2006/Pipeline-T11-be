package com.example.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegistrationDto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String name;

    @Size(max = 200, message = "El ingrediente activo no puede exceder 200 caracteres")
    private String activeIngredient;

    @Size(max = 100, message = "La presentación no puede exceder 100 caracteres")
    private String presentation;

    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imageUrl;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    private String productCode;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @NotNull(message = "La marca es obligatoria")
    private Long brandId;

    private String metadata;

    private Boolean isActive;
}