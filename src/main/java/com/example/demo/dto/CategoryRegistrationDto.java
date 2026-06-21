package com.example.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRegistrationDto {

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String description;

    private Long parentCategoryId;

    @Min(value = 0, message = "El nivel debe ser 0 o mayor")
    private Integer level;

    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String code;

    @Min(value = 0, message = "El orden de visualización debe ser 0 o mayor")
    private Integer orderDisplay;

    private Boolean isActive;
}