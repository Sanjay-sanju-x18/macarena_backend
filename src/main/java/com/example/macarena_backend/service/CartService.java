package com.example.macarena_backend.service;

import com.example.macarena_backend.entity.CartItem;
import com.example.macarena_backend.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository repo;

    public CartService(CartItemRepository repo) {
        this.repo = repo;
    }

    /**
     * Add item, or merge quantity if the same (customer, product, size) already exists.
     */
    @Transactional
    public CartItem addOrIncrement(Long customerId, Long productId, String size, int quantity) {
        String safeSize = (size == null || size.isBlank()) ? "One Size" : size.trim();
        int addQty = Math.max(1, quantity);

        CartItem existing = repo
                .findByCustomerIdAndProductIdAndSize(customerId, productId, safeSize)
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + addQty);
            return repo.save(existing);
        }

        return repo.save(new CartItem(customerId, productId, safeSize, addQty));
    }

    public List<CartItem> getCart(Long customerId) {
        return repo.findByCustomerIdOrderByIdDesc(customerId);
    }

    @Transactional
    public CartItem updateQuantity(Long customerId, Long cartItemId, int quantity) {
        CartItem item = repo.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Not your cart item");
        }

        if (quantity <= 0) {
            repo.delete(item);
            return null;
        }

        item.setQuantity(quantity);
        return repo.save(item);
    }

    @Transactional
    public void remove(Long customerId, Long cartItemId) {
        CartItem item = repo.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Not your cart item");
        }

        repo.delete(item);
    }

    @Transactional
    public void clear(Long customerId) {
        repo.deleteByCustomerId(customerId);
    }
}