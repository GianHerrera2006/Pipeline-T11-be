package com.example.demo.Service;

import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.dto.CartItemResponseDto;
import com.example.demo.dto.CartResponseDto;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public CartResponseDto getOrCreateCart(String cartToken) {
        Cart cart;
        if (cartToken == null || cartToken.isEmpty()) {
            cart = createNewCart();
        } else {
            cart = cartRepository.findByCartToken(cartToken)
                    .orElseGet(this::createNewCart);
        }
        return mapToResponseDto(cart);
    }

    private Cart createNewCart() {
        Cart cart = new Cart();
        cart.setCartToken(UUID.randomUUID().toString());
        return cartRepository.save(cart);
    }

    @Transactional
    public CartResponseDto addItem(String cartToken, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByCartToken(cartToken)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            existingItem.get().setUnitPrice(product.getBasePrice()); // Actualizar precio por si cambió
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getBasePrice());
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponseDto(savedCart);
    }

    @Transactional
    public CartResponseDto removeItem(String cartToken, Long productId) {
        Cart cart = cartRepository.findByCartToken(cartToken)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        Cart savedCart = cartRepository.save(cart);
        return mapToResponseDto(savedCart);
    }

    @Transactional
    public void clearCart(String cartToken) {
        Cart cart = cartRepository.findByCartToken(cartToken)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponseDto mapToResponseDto(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setCartToken(cart.getCartToken());
        
        dto.setItems(cart.getItems().stream().map(item -> {
            CartItemResponseDto itemDto = new CartItemResponseDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return itemDto;
        }).collect(Collectors.toList()));

        BigDecimal total = dto.getItems().stream()
                .map(CartItemResponseDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        dto.setTotal(total);
        
        // Sincronizar totalAmount en el modelo si es necesario
        if (cart.getTotalAmount() == null || cart.getTotalAmount().compareTo(total) != 0) {
            cart.setTotalAmount(total);
            cartRepository.save(cart);
        }

        return dto;
    }
}
