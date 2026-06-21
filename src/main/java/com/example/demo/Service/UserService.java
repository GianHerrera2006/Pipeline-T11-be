package com.example.demo.Service;

import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.StoreRepository;
import com.example.demo.dto.UserRegistrationDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.model.User;
import com.example.demo.model.Role;
import com.example.demo.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StoreRepository storeRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto) {
        // Validar duplicados
        if (userRepository.existsByDocNumber(dto.getDocNumber())) {
            throw new RuntimeException("El número de documento ya está registrado");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Obtener el rol
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        // Obtener la tienda si aplica
        Store store = null;
        if (dto.getStoreId() != null) {
            store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));
        }

        // Generar user_code automático
        String userCode = generateUserCode();

        // Crear usuario
        User user = new User();
        user.setUserCode(userCode);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDocType(dto.getDocType());
        user.setDocNumber(dto.getDocNumber());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setUbigeoId(dto.getUbigeoId());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setStore(store);
        user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        user.setDeletedAt(null);
        user.setRestoredAt(null);

        User savedUser = userRepository.save(user);
        return convertToResponseDto(savedUser);
    }

    public List<UserResponseDto> findAllActive() {
        return userRepository.findAllActive().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<UserResponseDto> findById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDto);
    }

    public Optional<UserResponseDto> findByUserCode(String userCode) {
        return userRepository.findByUserCode(userCode)
                .map(this::convertToResponseDto);
    }

    public List<UserResponseDto> findByRoleId(Long roleId) {
        return userRepository.findByRoleId(roleId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> findByStoreId(Long storeId) {
        return userRepository.findByStoreId(storeId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRegistrationDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar duplicados de documento (excepto el actual)
        if (!user.getDocNumber().equals(dto.getDocNumber()) && 
            userRepository.existsByDocNumber(dto.getDocNumber())) {
            throw new RuntimeException("El número de documento ya está registrado");
        }

        // Validar duplicados de email (excepto el actual)
        if (!user.getEmail().equals(dto.getEmail()) && 
            userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDocType(dto.getDocType());
        user.setDocNumber(dto.getDocNumber());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setUbigeoId(dto.getUbigeoId());

        // Actualizar contraseña solo si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Actualizar rol
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        user.setRole(role);

        // Actualizar tienda
        Store store = null;
        if (dto.getStoreId() != null) {
            store = storeRepository.findById(dto.getStoreId())
                    .orElseThrow(() -> new RuntimeException("Tienda no encontrada"));
        }
        user.setStore(store);

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }

        User updatedUser = userRepository.save(user);
        return convertToResponseDto(updatedUser);
    }

    @Transactional
    public void deleteLogical(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setIsActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void restore(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setIsActive(true);
        user.setDeletedAt(null);
        user.setRestoredAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Genera automáticamente el código de usuario con formato USR-NNN
     */
    private synchronized String generateUserCode() {
        // Obtener el último usuario registrado
        List<User> allUsers = userRepository.findAll();
        
        if (allUsers.isEmpty()) {
            return "USR-001";
        }

        // Buscar el código más alto
        int maxNumber = allUsers.stream()
                .map(u -> u.getUserCode())
                .filter(code -> code.startsWith("USR-"))
                .map(code -> {
                    try {
                        return Integer.parseInt(code.substring(4));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max(Integer::compare)
                .orElse(0);

        return String.format("USR-%03d", maxNumber + 1);
    }

    /**
     * Convierte una entidad User a UserResponseDto
     */
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUserCode(user.getUserCode());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDocType(user.getDocType());
        dto.setDocNumber(user.getDocNumber());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setUbigeoId(user.getUbigeoId());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleName(user.getRole().getName());
        }
        if (user.getStore() != null) {
            dto.setStoreId(user.getStore().getId());
            dto.setStoreName(user.getStore().getName());
        }
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
