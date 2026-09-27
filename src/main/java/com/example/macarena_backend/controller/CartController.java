package com.example.macarena_backend.controller;

import com.example.macarena_backend.entity.CartItem;
import com.example.macarena_backend.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    /** Extract customer id from the JWT authentication set by JwtAuthFilter. */
    private Long currentCustomerId(Authentication auth) {
        if (auth == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        Object details = auth.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        throw new IllegalArgumentException("Customer id missing from token");
    }

    // ---------- POST /api/cart ----------
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> body,
                                 Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);

            Long productId = Long.valueOf(body.get("productId").toString());
            String size = body.get("size") != null
                    ? body.get("size").toString()
                    : "One Size";
            int quantity = body.get("quantity") != null
                    ? Integer.parseInt(body.get("quantity").toString())
                    : 1;

            CartItem saved = service.addOrIncrement(customerId, productId, size, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- GET /api/cart ----------
    @GetMapping
    public ResponseEntity<?> getCart(Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            List<CartItem> items = service.getCart(customerId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- PUT /api/cart/{itemId} ----------
    @PutMapping("/{itemId}")
    public ResponseEntity<?> update(@PathVariable Long itemId,
                                    @RequestBody Map<String, Object> body,
                                    Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            int quantity = Integer.parseInt(body.get("quantity").toString());

            CartItem updated = service.updateQuantity(customerId, itemId, quantity);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- DELETE /api/cart/{itemId} ----------
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> remove(@PathVariable Long itemId,
                                    Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            service.remove(customerId, itemId);
            return ResponseEntity.ok(Map.of(
                    "message", "Cart item removed",
                    "id", itemId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- DELETE /api/cart ----------
    @DeleteMapping
    public ResponseEntity<?> clear(Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            service.clear(customerId);
            return ResponseEntity.ok(Map.of("message", "Cart cleared"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}