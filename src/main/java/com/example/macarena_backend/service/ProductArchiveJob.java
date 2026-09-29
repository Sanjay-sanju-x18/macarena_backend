package com.example.macarena_backend.service;

import com.example.macarena_backend.entity.Product;
import com.example.macarena_backend.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Archives fully sold-out products after a grace period.
 *
 * Once a product's totalQty hits 0 AND a sold-out record exists that is
 * older than the grace period, the product is archived (archived_at set).
 *
 * Result: it disappears from the home page (`getAll()` filters by
 * `archived_at IS NULL`) and direct links return "no longer available".
 */
@Component
public class ProductArchiveJob {

    private final ProductRepository productRepo;

    public ProductArchiveJob(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    /**
     * Grace period — how long a fully sold-out product stays visible
     * on the home page before being archived.
     *
     * TESTING:    2 minutes
     * PRODUCTION: 1440 (24 hours)
     */
    private static final long GRACE_MINUTES = 2;   // 👈 change for production

    /**
     * How often this sweep runs.
     *
     * TESTING:    30_000      (every 30 seconds)
     * PRODUCTION: 300_000     (every 5 minutes) — plenty
     */
    @Scheduled(fixedRate = 30_000)   // 👈 change for production
    @Transactional
    public void sweep() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(GRACE_MINUTES);

        List<Product> candidates = productRepo.findFullySoldOutCandidates(cutoff);

        if (candidates.isEmpty()) {
            // Uncomment the next line if you want to see the job is alive
            // System.out.println("[archive] sweep — nothing to archive");
            return;
        }

        for (Product p : candidates) {
            p.setArchivedAt(LocalDateTime.now());
            p.setArchivedReason("STOCK_ZERO");
            productRepo.save(p);

            System.out.println("[archive] Removed product " + p.getId()
                    + " - " + p.getDressName() + " from listings");
        }
    }
}