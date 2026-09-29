package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.SoldOutProductResponse;
import com.example.macarena_backend.entity.SoldOutProduct;
import com.example.macarena_backend.repository.SoldOutProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SoldOutProductService {

    private final SoldOutProductRepository repo;

    public SoldOutProductService(SoldOutProductRepository repo) {
        this.repo = repo;
    }

    /** Called by OrderService when a size hits 0. */
    @Transactional
    public void markSoldOut(Long productId, String sizeLabel, String productName) {
        // Don't create duplicates while an active flag exists
        List<SoldOutProduct> existing =
            repo.findByProductIdAndSizeLabelAndExpiresAtAfter(
                productId, sizeLabel, LocalDateTime.now());
        if (!existing.isEmpty()) return;

        repo.save(new SoldOutProduct(productId, sizeLabel, productName));
    }

    /** All active sold-out records for a single product. */
    public List<SoldOutProduct> getActiveForProduct(Long productId) {
        return repo.findByProductIdAndExpiresAtAfter(productId, LocalDateTime.now());
    }

    /** Set of size labels that are currently sold out for a product. */
    public Set<String> getSoldOutSizes(Long productId) {
        return repo.findByProductIdAndExpiresAtAfter(productId, LocalDateTime.now())
                   .stream()
                   .map(SoldOutProduct::getSizeLabel)
                   .collect(Collectors.toSet());
    }

    /** All active sold-out records (for admin view). */
    public List<SoldOutProductResponse> getAllActive() {
        return repo.findByExpiresAtAfter(LocalDateTime.now())
                   .stream()
                   .map(this::toDto)
                   .collect(Collectors.toList());
    }

    /** Manual removal (e.g. admin restocked). */
    @Transactional
    public void clear(Long productId, String sizeLabel) {
        List<SoldOutProduct> toRemove = repo.findByProductId(productId);
        toRemove.stream()
                .filter(s -> s.getSizeLabel() == null
                        || s.getSizeLabel().equalsIgnoreCase(sizeLabel))
                .forEach(repo::delete);
    }

    /** Cleanup job — called by a scheduler. */
    @Transactional
    public void purgeExpired() {
        repo.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private SoldOutProductResponse toDto(SoldOutProduct s) {
        return new SoldOutProductResponse(
            s.getId(), s.getProductId(), s.getProductName(),
            s.getSizeLabel(), s.getSoldOutAt(), s.getExpiresAt());
    }
}