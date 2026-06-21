package com.example.demo.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationDto {

    @NotBlank(message = "El código de cliente es obligatorio")
    @Size(max = 20, message = "El código de cliente no puede exceder 20 caracteres")
    private String customerCode;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 20, message = "El tipo de documento no puede exceder 20 caracteres")
    private String docType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String docNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 25, message = "El nombre debe tener entre 2 y 25 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 25, message = "El apellido debe tener entre 2 y 25 caracteres")
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe tener un formato válido")
    private String phone;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String address;

    private Integer ubigeoId;

    private String password;

    @Min(value = 0, message = "Las visitas totales no pueden ser negativas")
    private Integer totalVisits;

    private LocalDate lastPurchaseDate;

    private LocalDate customerSince;

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;

    private Boolean isActive;
}