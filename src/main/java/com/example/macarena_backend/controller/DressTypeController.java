package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.DressTypeRequest;
import com.example.macarena_backend.entity.DressType;
import com.example.macarena_backend.service.DressTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dress-types")
@CrossOrigin(origins = "http://localhost:4200")
public class DressTypeController {

    private final DressTypeService service;

    public DressTypeController(DressTypeService service) {
        this.service = service;
    }

    // GET all
    @GetMapping
    public List<DressType> getAll() {
        return service.getAll();
    }

    // GET one by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // POST add
    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody DressTypeRequest request) {
        try {
            DressType saved = service.add(request.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // PUT update by id
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody DressTypeRequest request) {
        try {
            DressType updated = service.update(id, request.getName());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            HttpStatus status = "Dress type not found".equals(msg)
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.CONFLICT;
            return ResponseEntity.status(status).body(Map.of("error", msg));
        }
    }

    // DELETE by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        try {
            service.remove(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}