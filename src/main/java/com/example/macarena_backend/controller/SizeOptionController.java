package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.SizeOptionRequest;
import com.example.macarena_backend.dto.SizeOptionResponse;
import com.example.macarena_backend.service.SizeOptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sizes")
@CrossOrigin(origins = "http://localhost:4200")
public class SizeOptionController {

    private final SizeOptionService service;

    public SizeOptionController(SizeOptionService service) {
        this.service = service;
    }

    // GET all, or filter by type
    //   /api/sizes
    //   /api/sizes?type=alphabet
    //   /api/sizes?type=number
    @GetMapping
    public List<SizeOptionResponse> getAll(
            @RequestParam(required = false) String type) {
        return (type != null && !type.isBlank())
                ? service.getByType(type.toLowerCase())
                : service.getAll();
    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody SizeOptionRequest request) {
        try {
            SizeOptionResponse saved = service.add(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        try {
            service.remove(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Size deleted successfully",
                    "id", id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}