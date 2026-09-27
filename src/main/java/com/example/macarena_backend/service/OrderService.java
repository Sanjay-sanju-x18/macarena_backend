package com.example.macarena_backend.service;

import com.example.macarena_backend.dto.OrderRequest;
import com.example.macarena_backend.dto.OrderResponse;
import com.example.macarena_backend.entity.*;
import com.example.macarena_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo,
                        CartItemRepository cartRepo,
                        ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    // ---------- READ ----------
    public List<OrderResponse> getCustomerOrders(Long customerId) {
        return orderRepo.findByCustomerIdOrderByIdDesc(customerId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepo.findAllByOrderByIdDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long orderId, Long customerId, boolean isAdmin) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!isAdmin && !o.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Not your order");
        }
        return toDto(o);
    }

    // ---------- PLACE ORDER FROM CART ----------
    @Transactional
    public OrderResponse placeOrderFromCart(Long customerId, OrderRequest req) {
        List<CartItem> cart = cartRepo.findByCustomerIdOrderByIdDesc(customerId);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setStatus("PENDING");
        order.setPaymentMethod("UPI");
        order.setPaymentReference(req.getPaymentReference());
        order.setFullName(req.getFullName());
        order.setPhoneNumber(req.getPhoneNumber());
        order.setAddressLine(req.getAddressLine());
        order.setCity(req.getCity());
        order.setState(req.getState());
        order.setPincode(req.getPincode());

        double total = 0;
        List<OrderItem> items = new ArrayList<>();

        for (CartItem ci : cart) {
            Product p = productRepo.findById(ci.getProductId()).orElse(null);
            if (p == null) continue;

            double unitPrice = (p.getOfferPrice() != null && p.getOfferPrice() > 0)
                    ? p.getOfferPrice()
                    : p.getPrice();

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(p.getId());
            oi.setProductName(p.getDressName());
            oi.setProductImage(p.getPhotos().isEmpty() ? null : p.getPhotos().get(0));
            oi.setSize(ci.getSize());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(unitPrice);
            oi.setLineTotal(unitPrice * ci.getQuantity());

            items.add(oi);
            total += oi.getLineTotal();
        }

        order.setItems(items);
        order.setTotalAmount(total);

        Order saved = orderRepo.save(order);

        // Clear the cart after successful order
        cartRepo.deleteAll(cart);

        return toDto(saved);
    }

    // ---------- BUY NOW (single item) ----------
    @Transactional
    public OrderResponse placeSingleItemOrder(Long customerId,
                                              OrderRequest req,
                                              Long productId,
                                              String size,
                                              int quantity) {
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        double unitPrice = (p.getOfferPrice() != null && p.getOfferPrice() > 0)
                ? p.getOfferPrice()
                : p.getPrice();

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setStatus("PENDING");
        order.setPaymentMethod("UPI");
        order.setPaymentReference(req.getPaymentReference());
        order.setFullName(req.getFullName());
        order.setPhoneNumber(req.getPhoneNumber());
        order.setAddressLine(req.getAddressLine());
        order.setCity(req.getCity());
        order.setState(req.getState());
        order.setPincode(req.getPincode());

        OrderItem oi = new OrderItem();
        oi.setOrder(order);
        oi.setProductId(p.getId());
        oi.setProductName(p.getDressName());
        oi.setProductImage(p.getPhotos().isEmpty() ? null : p.getPhotos().get(0));
        oi.setSize(size == null || size.isBlank() ? "One Size" : size);
        oi.setQuantity(quantity);
        oi.setUnitPrice(unitPrice);
        oi.setLineTotal(unitPrice * quantity);

        order.setItems(List.of(oi));
        order.setTotalAmount(oi.getLineTotal());

        Order saved = orderRepo.save(order);
        return toDto(saved);
    }

    // ---------- ADMIN: UPDATE STATUS ----------
    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        Order o = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        o.setStatus(status.toUpperCase());
        return toDto(orderRepo.save(o));
    }

    // ---------- MAPPER ----------
    private OrderResponse toDto(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setCustomerId(o.getCustomerId());
        r.setTotalAmount(o.getTotalAmount());
        r.setStatus(o.getStatus());
        r.setPaymentMethod(o.getPaymentMethod());
        r.setPaymentReference(o.getPaymentReference());
        r.setFullName(o.getFullName());
        r.setPhoneNumber(o.getPhoneNumber());
        r.setAddressLine(o.getAddressLine());
        r.setCity(o.getCity());
        r.setState(o.getState());
        r.setPincode(o.getPincode());
        r.setCreatedAt(o.getCreatedAt());

        List<OrderResponse.OrderItemDto> itemDtos = o.getItems().stream().map(i -> {
            OrderResponse.OrderItemDto d = new OrderResponse.OrderItemDto();
            d.setProductId(i.getProductId());
            d.setProductName(i.getProductName());
            d.setProductImage(i.getProductImage());
            d.setSize(i.getSize());
            d.setQuantity(i.getQuantity());
            d.setUnitPrice(i.getUnitPrice());
            d.setLineTotal(i.getLineTotal());
            return d;
        }).collect(Collectors.toList());

        r.setItems(itemDtos);
        return r;
    }
}