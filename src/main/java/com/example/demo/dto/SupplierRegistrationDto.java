package com.example.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRegistrationDto {

    @NotBlank(message = "El código del proveedor es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String supplierCode;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String farmName;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String docType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String docNumber;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe tener un formato válido")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String address;

    private Integer ubigeoId;

    @Size(max = 100, message = "El contacto no puede exceder 100 caracteres")
    private String contactPerson;

    private Boolean isActive;
}