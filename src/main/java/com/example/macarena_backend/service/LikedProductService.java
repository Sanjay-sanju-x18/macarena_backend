package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.LikedProductResponse;
import com.example.macarena_backend.entity.LikedProduct;
import com.example.macarena_backend.entity.Product;
import com.example.macarena_backend.repository.LikedProductRepository;
import com.example.macarena_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikedProductService {

    private final LikedProductRepository repo;
    private final ProductRepository productRepo;

    public LikedProductService(LikedProductRepository repo,
                               ProductRepository productRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
    }

    /** Just IDs — used by the heart's isLiked() check. */
    public List<Long> getLikedProductIds(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                   .stream()
                   .map(LikedProduct::getProductId)
                   .collect(Collectors.toList());
    }

    /** Enriched list — used by /liked-products page. */
    public List<LikedProductResponse> getLikedProducts(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(lp -> {
                Product p = productRepo.findById(lp.getProductId()).orElse(null);
                if (p == null) {
                    return new LikedProductResponse(lp.getProductId(), null, null, null);
                }

                // First photo from Product.photos (List<String>)
                String imagePath = (p.getPhotos() != null && !p.getPhotos().isEmpty())
                        ? p.getPhotos().get(0)
                        : null;

                // Prefer offer price, else regular price
                Double price = (p.getOfferPrice() != null && p.getOfferPrice() > 0)
                        ? p.getOfferPrice()
                        : p.getPrice();

                return new LikedProductResponse(
                        p.getId(),
                        p.getDressName(),
                        imagePath,
                        price
                );
            })
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