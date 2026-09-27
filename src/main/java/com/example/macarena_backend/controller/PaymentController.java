package com.example.macarena_backend.controller;

import com.example.macarena_backend.config.PaymentProperties;
import com.example.macarena_backend.config.RazorpayConfig;
import com.example.macarena_backend.dto.CreatePaymentRequest;
import com.example.macarena_backend.dto.OrderRequest;
import com.example.macarena_backend.dto.OrderResponse;
import com.example.macarena_backend.dto.VerifyPaymentRequest;
import com.example.macarena_backend.service.OrderService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;
    private final OrderService orderService;
    private final PaymentProperties paymentProperties;

    public PaymentController(RazorpayClient razorpayClient,
                             RazorpayConfig razorpayConfig,
                             OrderService orderService,
                             PaymentProperties paymentProperties) {
        this.razorpayClient = razorpayClient;
        this.razorpayConfig = razorpayConfig;
        this.orderService = orderService;
        this.paymentProperties = paymentProperties;
    }

    // ---------- Helper ----------
    private Long currentCustomerId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Not authenticated");
        Object d = auth.getDetails();
        if (d instanceof Long) return (Long) d;
        throw new IllegalArgumentException("Customer id missing from token");
    }

    // ============================================================
    // Step 1 — Create Razorpay order
    // ============================================================
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreatePaymentRequest req) {
        try {
            // Razorpay expects amount in paise (multiply rupees by 100)
            int amountInPaise = (int) Math.round(req.getAmount() * 100);

            JSONObject orderReq = new JSONObject();
            orderReq.put("amount", amountInPaise);
            orderReq.put("currency", "INR");
            orderReq.put("receipt", "rcpt_" + System.currentTimeMillis());
            orderReq.put("payment_capture", 1);

            Order razorpayOrder = razorpayClient.orders.create(orderReq);

            return ResponseEntity.ok(Map.of(
                    "key", razorpayConfig.getKeyId(),
                    "razorpayOrderId", razorpayOrder.get("id"),
                    "amount", amountInPaise,
                    "currency", "INR"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to create payment order: " + e.getMessage()));
        }
    }

    // ============================================================
    // Step 2 — Verify Razorpay signature + place order
    // ============================================================
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyPaymentRequest req,
                                    Authentication auth) {
        try {
            // 1. Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", req.getRazorpayOrderId());
            options.put("razorpay_payment_id", req.getRazorpayPaymentId());
            options.put("razorpay_signature", req.getRazorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(options, razorpayConfig.getKeySecret());

            if (!valid) {
                return ResponseEntity.status(400)
                        .body(Map.of("error", "Payment signature verification failed"));
            }

            // 2. Signature valid → place the real order
            Long customerId = currentCustomerId(auth);

            OrderRequest orderReq = new OrderRequest();
            orderReq.setFullName(req.getFullName());
            orderReq.setPhoneNumber(req.getPhoneNumber());
            orderReq.setAddressLine(req.getAddressLine());
            orderReq.setCity(req.getCity());
            orderReq.setState(req.getState());
            orderReq.setPincode(req.getPincode());
            orderReq.setPaymentReference(req.getRazorpayPaymentId());

            OrderResponse order;
            if ("buy-now".equalsIgnoreCase(req.getMode())) {
                order = orderService.placeSingleItemOrder(
                        customerId,
                        orderReq,
                        req.getProductId(),
                        req.getSize() != null ? req.getSize() : "One Size",
                        req.getQuantity() != null ? req.getQuantity() : 1
                );
            } else {
                order = orderService.placeOrderFromCart(customerId, orderReq);
            }

            // Mark as PAID
            orderService.updateStatus(order.getId(), "PAID");

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // Trial — save order without payment
    // (only works when payment.required=false)
    // ============================================================
    @PostMapping("/trial-place-order")
    public ResponseEntity<?> trialPlaceOrder(@Valid @RequestBody VerifyPaymentRequest req,
                                             Authentication auth) {
        // 🛡️ Safety: this endpoint is dead once real payments are enabled
        if (paymentProperties.isPaymentRequired()) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Trial mode is disabled. Please complete payment."));
        }

        try {
            Long customerId = currentCustomerId(auth);

            OrderRequest orderReq = new OrderRequest();
            orderReq.setFullName(req.getFullName());
            orderReq.setPhoneNumber(req.getPhoneNumber());
            orderReq.setAddressLine(req.getAddressLine());
            orderReq.setCity(req.getCity());
            orderReq.setState(req.getState());
            orderReq.setPincode(req.getPincode());
            orderReq.setPaymentReference("TRIAL-NO-PAYMENT");

            OrderResponse order;
            if ("buy-now".equalsIgnoreCase(req.getMode())) {
                order = orderService.placeSingleItemOrder(
                        customerId,
                        orderReq,
                        req.getProductId(),
                        req.getSize() != null ? req.getSize() : "One Size",
                        req.getQuantity() != null ? req.getQuantity() : 1
                );
            } else {
                order = orderService.placeOrderFromCart(customerId, orderReq);
            }

            // Mark status as TRIAL
            orderService.updateStatus(order.getId(), "TRIAL");

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}