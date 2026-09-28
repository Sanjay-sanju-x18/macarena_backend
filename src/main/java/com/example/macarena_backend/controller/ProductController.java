package com.example.macarena_backend.controller;

import com.example.macarena_backend.dto.ProductRequest;
import com.example.macarena_backend.dto.ProductResponse;
import com.example.macarena_backend.service.ProductService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService service;
    private final Validator validator;

    public ProductController(ProductService service, Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> add(
            @RequestParam String dressName,
            @RequestParam Long dressTypeId,                    // ✅
            @RequestParam Double price,
            @RequestParam(required = false) Double offerPercentage,
            @RequestParam Double offerPrice,
            @RequestParam String sizeType,
            @RequestParam Integer totalQty,
            @RequestParam("sizeLabels") List<String> sizeLabels,
            @RequestParam("sizeQty") List<Integer> sizeQty,
            @RequestParam("photos") List<MultipartFile> photos) {

        try {
            ProductRequest req = new ProductRequest();
            req.setDressName(dressName);
            req.setDressTypeId(dressTypeId);                  // ✅
            req.setPrice(price);
            req.setOfferPercentage(offerPercentage);
            req.setOfferPrice(offerPrice);
            req.setSizeType(sizeType);
            req.setTotalQty(totalQty);

            if (sizeLabels.size() != sizeQty.size()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "sizeLabels and sizeQty count mismatch"));
            }

            List<ProductRequest.SizeQtyDto> sizes = new ArrayList<>();
            for (int i = 0; i < sizeLabels.size(); i++) {
                ProductRequest.SizeQtyDto dto = new ProductRequest.SizeQtyDto();
                dto.setSize(sizeLabels.get(i));
                dto.setQty(sizeQty.get(i));
                sizes.add(dto);
            }
            req.setSizes(sizes);

            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(req);
            if (!violations.isEmpty()) {
                String msg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }

            ProductResponse saved = service.create(req, photos);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam String dressName,
            @RequestParam Long dressTypeId,
            @RequestParam Double price,
            @RequestParam(required = false) Double offerPercentage,
            @RequestParam Double offerPrice,
            @RequestParam String sizeType,
            @RequestParam Integer totalQty,
            @RequestParam("sizeLabels") List<String> sizeLabels,
            @RequestParam("sizeQty") List<Integer> sizeQty,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {

        try {
            if (sizeLabels.size() != sizeQty.size()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "sizeLabels and sizeQty count mismatch"));
            }

            ProductRequest req = new ProductRequest();
            req.setDressName(dressName);
            req.setDressTypeId(dressTypeId);
            req.setPrice(price);
            req.setOfferPercentage(offerPercentage);
            req.setOfferPrice(offerPrice);
            req.setSizeType(sizeType);
            req.setTotalQty(totalQty);

            List<ProductRequest.SizeQtyDto> sizes = new ArrayList<>();
            for (int i = 0; i < sizeLabels.size(); i++) {
                ProductRequest.SizeQtyDto dto = new ProductRequest.SizeQtyDto();
                dto.setSize(sizeLabels.get(i));
                dto.setQty(sizeQty.get(i));
                sizes.add(dto);
            }
            req.setSizes(sizes);

            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(req);
            if (!violations.isEmpty()) {
                String msg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(Map.of("error", msg));
            }

            ProductResponse updated = service.update(id, req, photos);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        try {
            service.remove(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Product deleted successfully",
                    "id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}