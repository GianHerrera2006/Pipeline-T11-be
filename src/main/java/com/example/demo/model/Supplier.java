package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long idSupplier;

    @NotBlank(message = "El código del proveedor es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Column(name = "supplier_code", nullable = false, length = 20, unique = true)
    private String supplierCode;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del proveedor debe tener entre 2 y 100 caracteres")
    @JsonProperty("farmName")
    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 20, message = "El tipo de documento no puede exceder 20 caracteres")
    @Column(name = "doc_type", nullable = false, length = 20)
    private String docType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    @Column(name = "doc_number", nullable = false, length = 20, unique = true)
    private String docNumber;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe tener un formato válido")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "ubigeo_id")
    private Integer ubigeoId;

    @Size(max = 100, message = "El contacto no puede exceder 100 caracteres")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Auditoría
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "restored_at")
    private LocalDateTime restoredAt;
}