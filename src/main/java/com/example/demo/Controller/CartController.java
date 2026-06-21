package com.example.demo.Controller;

import com.example.demo.Service.CartService;
import com.example.demo.dto.CartResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@RequestParam(required = false) String cartToken) {
        return ResponseEntity.ok(cartService.getOrCreateCart(cartToken));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addItem(
            @RequestParam String cartToken,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addItem(cartToken, productId, quantity));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartResponseDto> removeItem(
            @RequestParam String cartToken,
            @RequestParam Long productId) {
        return ResponseEntity.ok(cartService.removeItem(cartToken, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam String cartToken) {
        cartService.clearCart(cartToken);
        return ResponseEntity.ok().build();
    }
}
