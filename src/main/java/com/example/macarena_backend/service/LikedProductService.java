package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.LikedProductResponse;
import com.example.macarena_backend.entity.LikedProduct;
import com.example.macarena_backend.repository.LikedProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikedProductService {

    private final LikedProductRepository repo;

    public LikedProductService(LikedProductRepository repo) {
        this.repo = repo;
    }

    /** Just the IDs — this is what the heart button uses. */
    public List<Long> getLikedProductIds(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                   .stream()
                   .map(LikedProduct::getProductId)
                   .collect(Collectors.toList());
    }

    /**
     * Enriched list for /liked-products page.
     * TODO: inject your ProductRepository here and populate name/image/price.
     * For now returns IDs only (frontend will handle placeholder).
     */
    public List<LikedProductResponse> getLikedProducts(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                   .stream()
                   .map(lp -> new LikedProductResponse(lp.getProductId(), null, null, null))
                   .collect(Collectors.toList());
    }

    public boolean isLiked(Long userId, Long productId) {
        return repo.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void add(Long userId, Long productId) {
        if (repo.existsByUserIdAndProductId(userId, productId)) return;
        repo.save(new LikedProduct(userId, productId));
    }

    @Transactional
    public void remove(Long userId, Long productId) {
        repo.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public boolean toggle(Long userId, Long productId) {
        if (repo.existsByUserIdAndProductId(userId, productId)) {
            repo.deleteByUserIdAndProductId(userId, productId);
            return false;
        }
        repo.save(new LikedProduct(userId, productId));
        return true;
    }

    public long count(Long userId) {
        return repo.countByUserId(userId);
    }
}