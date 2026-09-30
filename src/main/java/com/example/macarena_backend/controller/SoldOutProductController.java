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

    /** Admin: ella inactive (sold-out) products. */
    @GetMapping
    public ResponseEntity<List<SoldOutProductResponse>> listAll() {
        return ResponseEntity.ok(service.getAllActive());
    }

    /** Public: oru product-oda sold-out sizes. */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<String>> forProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getSoldOutSizes(productId).stream().toList());
    }

    /** Admin: oru size-a activate pannu. */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clear(@RequestParam Long productId,
                                                     @RequestParam String sizeLabel) {
        service.clear(productId, sizeLabel);
        return ResponseEntity.ok(Map.of("message", "Activated"));
    }

    /** Admin: product-oda ella sizes-um activate pannu. */
    @DeleteMapping("/activate/{productId}")
    public ResponseEntity<Map<String, String>> activateProduct(@PathVariable Long productId) {
        service.activateAll(productId);
        return ResponseEntity.ok(Map.of("message", "Product activated"));
    }

    /** Admin: record id vachu activate pannu. */
    @DeleteMapping("/record/{id}")
    public ResponseEntity<Map<String, String>> activateById(@PathVariable Long id) {
        service.activateById(id);
        return ResponseEntity.ok(Map.of("message", "Activated"));
    }
}