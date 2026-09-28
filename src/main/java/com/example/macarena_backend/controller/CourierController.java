package com.example.macarena_backend.controller;

import com.example.macarena_backend.entity.Courier;
import com.example.macarena_backend.service.CourierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/couriers")
public class CourierController {

    private final CourierService service;

    public CourierController(CourierService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Courier courier, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", br.getFieldErrors().get(0).getDefaultMessage()));
        }
        try {
            return ResponseEntity.ok(service.create(courier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody Courier courier, BindingResult br) {
        if (br.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", br.getFieldErrors().get(0).getDefaultMessage()));
        }
        try {
            return ResponseEntity.ok(service.update(id, courier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("message", "Courier deleted"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}