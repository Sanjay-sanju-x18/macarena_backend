package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.SoldOutProductResponse;
import com.example.macarena_backend.service.SoldOutProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sold-out")
@CrossOrigin(origins = "http://localhost:4200")
public class SoldOutProductController {

    private final SoldOutProductService service;

    public SoldOutProductController(SoldOutProductService service) {
        this.service = service;
    }

    /** Public — anyone can read which products/sizes are sold out. */
    @GetMapping
    public ResponseEntity<List<SoldOutProductResponse>> listAll() {
        return ResponseEntity.ok(service.getAllActive());
    }

    /** Public — sold-out sizes for one product. */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<String>> forProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(
            service.getSoldOutSizes(productId).stream().toList());
    }

    /** Admin — clear a sold-out flag (e.g. after restock). */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clear(
            @RequestParam Long productId,
            @RequestParam String sizeLabel) {
        service.clear(productId, sizeLabel);
        return ResponseEntity.ok(Map.of("message", "Cleared"));
    }
}