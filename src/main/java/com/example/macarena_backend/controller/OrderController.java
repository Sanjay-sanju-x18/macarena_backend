package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.OrderRequest;
import com.example.macarena_backend.dto.OrderResponse;
import com.example.macarena_backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    private Long currentCustomerId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Not authenticated");
        Object d = auth.getDetails();
        if (d instanceof Long) return (Long) d;
        throw new IllegalArgumentException("Customer id missing from token");
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // GET /api/orders  — customer sees own, admin sees all
    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        try {
            if (isAdmin(auth)) {
                return ResponseEntity.ok(service.getAllOrders());
            }
            return ResponseEntity.ok(service.getCustomerOrders(currentCustomerId(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id, Authentication auth) {
        try {
            boolean admin = isAdmin(auth);
            Long customerId = admin ? null : currentCustomerId(auth);
            return ResponseEntity.ok(service.getOrder(id, customerId, admin));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/orders/checkout — place order from cart
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@Valid @RequestBody OrderRequest req,
                                      Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);
            OrderResponse saved = service.placeOrderFromCart(customerId, req);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/orders/buy-now — place single-item order
    @PostMapping("/buy-now")
    public ResponseEntity<?> buyNow(@Valid @RequestBody Map<String, Object> body,
                                    Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);

            Long productId = Long.valueOf(body.get("productId").toString());
            String size = body.get("size") != null ? body.get("size").toString() : "One Size";
            int qty = body.get("quantity") != null
                    ? Integer.parseInt(body.get("quantity").toString())
                    : 1;

            OrderRequest req = new OrderRequest();
            req.setFullName((String) body.get("fullName"));
            req.setPhoneNumber((String) body.get("phoneNumber"));
            req.setAddressLine((String) body.get("addressLine"));
            req.setCity((String) body.get("city"));
            req.setState((String) body.get("state"));
            req.setPincode((String) body.get("pincode"));
            req.setPaymentReference((String) body.getOrDefault("paymentReference", null));

            return ResponseEntity.ok(
                    service.placeSingleItemOrder(customerId, req, productId, size, qty));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/orders/{id}/status — admin only
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body,
                                          Authentication auth) {
        try {
            if (!isAdmin(auth)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin only"));
            }
            return ResponseEntity.ok(service.updateStatus(id, body.get("status")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}