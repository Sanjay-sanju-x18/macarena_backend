package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.AdminRequest;
import com.example.macarena_backend.dto.AdminResponse;
import com.example.macarena_backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    // GET all admins
    @GetMapping
    public List<AdminResponse> getAll() {
        return service.getAll();
    }

    // POST create admin
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AdminRequest request) {
        try {
            AdminResponse saved = service.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        try {
            service.remove(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Admin deleted successfully",
                    "id", id
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}