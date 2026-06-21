package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String userCode;
    private String firstName;
    private String lastName;
    private String docType;
    private String docNumber;
    private String email;
    private String phone;
    private String address;
    private Integer ubigeoId;
    private String roleName;
    private Long roleId;
    private String storeName;
    private Long storeId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
