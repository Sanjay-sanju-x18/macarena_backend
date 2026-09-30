package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.SoldOutProductResponse;
import com.example.macarena_backend.entity.SoldOutProduct;
import com.example.macarena_backend.repository.SoldOutProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    /** OrderService stock 0 aana apo call pannum -> product INACTIVE aagum. */
    @Transactional
    public void markSoldOut(Long productId, String sizeLabel, String productName) {
        List<SoldOutProduct> existing =
                repo.findActiveByProductIdAndSize(productId, sizeLabel, LocalDateTime.now());
        if (!existing.isEmpty()) return; // already inactive

        repo.save(new SoldOutProduct(productId, sizeLabel, productName));
    }

    /** Oru product-oda inactive records. */
    public List<SoldOutProduct> getActiveForProduct(Long productId) {
        return repo.findActiveByProductId(productId, LocalDateTime.now());
    }

    /** Oru product-la sold-out aana size labels (cart/product page-ku). */
    public Set<String> getSoldOutSizes(Long productId) {
        return repo.findActiveByProductId(productId, LocalDateTime.now())
                .stream()
                .map(SoldOutProduct::getSizeLabel)
                .collect(Collectors.toSet());
    }

    /** Product ellaa sizes-um sold out-a nu check. */
    public boolean isProductInactive(Long productId) {
        return !repo.findActiveByProductId(productId, LocalDateTime.now()).isEmpty();
    }

    /** Admin list: ella inactive products. */
    public List<SoldOutProductResponse> getAllActive() {
        return repo.findAllActive(LocalDateTime.now())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** ACTIVATE: oru size mattum. */
    @Transactional
    public void clear(Long productId, String sizeLabel) {
        List<SoldOutProduct> toRemove =
                repo.findActiveByProductIdAndSize(productId, sizeLabel, LocalDateTime.now());
        if (toRemove.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No sold-out record for this product/size");
        }
        repo.deleteAll(toRemove);
    }

    /** ACTIVATE: product-oda ella sizes-um. */
    @Transactional
    public void activateAll(Long productId) {
        List<SoldOutProduct> toRemove = repo.findByProductId(productId);
        if (toRemove.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product is already active");
        }
        repo.deleteAll(toRemove);
    }

    /** ACTIVATE: record id vachu. */
    @Transactional
    public void activateById(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
        }
        repo.deleteById(id);
    }

    /** Scheduler: expired records-a delete pannum. */
    @Transactional
    public void purgeExpired() {
        repo.deleteExpired(LocalDateTime.now());
    }

    private SoldOutProductResponse toDto(SoldOutProduct s) {
        return new SoldOutProductResponse(
                s.getId(), s.getProductId(), s.getProductName(),
                s.getSizeLabel(), s.getSoldOutAt(), s.getExpiresAt());
    }
}