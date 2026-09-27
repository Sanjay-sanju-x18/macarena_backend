package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.LikedProductResponse;
import com.example.macarena_backend.service.LikedProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:4200")
public class LikedProductController {

    private final LikedProductService service;

    public LikedProductController(LikedProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Long>> getLikes(Authentication auth) {
        return ResponseEntity.ok(service.getLikedProductIds(currentUserId(auth)));
    }

    @GetMapping("/details")
    public ResponseEntity<List<LikedProductResponse>> getLikesDetailed(Authentication auth) {
        return ResponseEntity.ok(service.getLikedProducts(currentUserId(auth)));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count(Authentication auth) {
        return ResponseEntity.ok(Map.of("count", service.count(currentUserId(auth))));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable Long productId,
            Authentication auth) {
        return ResponseEntity.ok(Map.of("liked", service.isLiked(currentUserId(auth), productId)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Boolean>> like(
            @PathVariable Long productId,
            Authentication auth) {
        service.add(currentUserId(auth), productId);
        return ResponseEntity.ok(Map.of("liked", true));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Boolean>> unlike(
            @PathVariable Long productId,
            Authentication auth) {
        service.remove(currentUserId(auth), productId);
        return ResponseEntity.ok(Map.of("liked", false));
    }

    @PostMapping("/toggle/{productId}")
    public ResponseEntity<Map<String, Boolean>> toggle(
            @PathVariable Long productId,
            Authentication auth) {
        boolean nowLiked = service.toggle(currentUserId(auth), productId);
        return ResponseEntity.ok(Map.of("liked", nowLiked));
    }

    // ============================================================
    // Same pattern as CartController — read customer id from
    // auth.getDetails(), where JwtAuthFilter stashes it.
    // ============================================================
    private Long currentUserId(Authentication auth) {
        if (auth == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        Object details = auth.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        throw new IllegalArgumentException("Customer id missing from token");
    }
}