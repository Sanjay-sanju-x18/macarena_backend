package com.example.macarena_backend.controller;

import com.cashfree.pg.ApiResponse;
import com.cashfree.pg.Cashfree;
import com.cashfree.pg.model.CreateOrderRequest;
import com.cashfree.pg.model.CustomerDetails;
import com.cashfree.pg.model.OrderEntity;
import com.example.macarena_backend.dto.CreatePaymentRequest;
import com.example.macarena_backend.dto.OrderRequest;
import com.example.macarena_backend.dto.OrderResponse;
import com.example.macarena_backend.dto.VerifyPaymentRequest;
import com.example.macarena_backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final Cashfree cashfree;
    private final OrderService orderService;

    public PaymentController(Cashfree cashfree, OrderService orderService) {
        this.cashfree = cashfree;
        this.orderService = orderService;
    }

    // ---------- Helper ----------
    private Long currentCustomerId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Not authenticated");
        Object d = auth.getDetails();
        if (d instanceof Long) return (Long) d;
        throw new IllegalArgumentException("Customer id missing from token");
    }

    // ============================================================
    // STEP 1 — Create Cashfree order
    // POST /api/payments/cashfree/create-order
    // ============================================================
    @PostMapping("/cashfree/create-order")
    public ResponseEntity<?> createCashfreeOrder(
            @Valid @RequestBody CreatePaymentRequest req,
            Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);

            // Customer details
            CustomerDetails customer = new CustomerDetails();
            customer.setCustomerId(String.valueOf(customerId));
            customer.setCustomerPhone(
                    req.getPhoneNumber() != null ? req.getPhoneNumber() : "9999999999");
            if (req.getEmail() != null) {
                customer.setCustomerEmail(req.getEmail());
            }

            // Build Cashfree order
            CreateOrderRequest orderReq = new CreateOrderRequest();
            orderReq.setOrderAmount(BigDecimal.valueOf(req.getAmount()));
            orderReq.setOrderCurrency("INR");
            orderReq.setCustomerDetails(customer);
            String merchantOrderId = "MAC-" + System.currentTimeMillis()
                    + "-" + UUID.randomUUID().toString().substring(0, 6);
            orderReq.setOrderId(merchantOrderId);

            System.out.println(">>> [create-order] Sending to Cashfree:");
            System.out.println("    merchantOrderId = " + merchantOrderId);
            System.out.println("    amount          = " + req.getAmount());

            // Call Cashfree
            ApiResponse<OrderEntity> response =
                    cashfree.PGCreateOrder(orderReq, null, null, null);

            OrderEntity data = response.getData();

            System.out.println(">>> [create-order] Cashfree response:");
            System.out.println("    orderId     = " + data.getOrderId());
            System.out.println("    cfOrderId   = " + data.getCfOrderId());
            System.out.println("    orderStatus = " + data.getOrderStatus());
            System.out.println("    sessionId   = " + data.getPaymentSessionId());

            String returnedOrderId = data.getOrderId() != null
                    ? data.getOrderId()
                    : merchantOrderId;

            System.out.println(">>> [create-order] Returning orderId = " + returnedOrderId);

            return ResponseEntity.ok(Map.of(
                    "paymentSessionId", data.getPaymentSessionId(),
                    "orderId", returnedOrderId
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to create payment order: " + e.getMessage()));
        }
    }

    // ============================================================
    // STEP 2 — Verify Cashfree payment + place order
    // POST /api/payments/cashfree/verify
    // ============================================================
    @PostMapping("/cashfree/verify")
    public ResponseEntity<?> verifyCashfreePayment(
            @Valid @RequestBody VerifyPaymentRequest req,
            Authentication auth) {
        try {
            Long customerId = currentCustomerId(auth);

            System.out.println(">>> [verify] Received orderId from frontend = "
                    + req.getCashfreeOrderId());

            // Fetch order from Cashfree
            ApiResponse<OrderEntity> response =
                    cashfree.PGFetchOrder(req.getCashfreeOrderId(), null, null, null);

            OrderEntity order = response.getData();
            String status = order.getOrderStatus();

            System.out.println(">>> [verify] Cashfree response:");
            System.out.println("    orderId   = " + order.getOrderId());
            System.out.println("    cfOrderId = " + order.getCfOrderId());
            System.out.println("    status    = " + status);

            if (!"PAID".equalsIgnoreCase(status)) {
                return ResponseEntity.status(400)
                        .body(Map.of("error",
                                "Payment not completed. Cashfree status: " + status));
            }

            // Build OrderRequest for our DB
            OrderRequest orderReq = new OrderRequest();
            orderReq.setFullName(req.getFullName());
            orderReq.setPhoneNumber(req.getPhoneNumber());
            orderReq.setAddressLine(req.getAddressLine());
            orderReq.setCity(req.getCity());
            orderReq.setState(req.getState());
            orderReq.setPincode(req.getPincode());
            orderReq.setPaymentReference(req.getCashfreeOrderId());

            OrderResponse placed;
            if ("buy-now".equalsIgnoreCase(req.getMode())) {
                placed = orderService.placeSingleItemOrder(
                        customerId,
                        orderReq,
                        req.getProductId(),
                        req.getSize() != null ? req.getSize() : "One Size",
                        req.getQuantity() != null ? req.getQuantity() : 1
                );
            } else {
                placed = orderService.placeOrderFromCart(customerId, orderReq);
            }

            orderService.updateStatus(placed.getId(), "PAID");
            return ResponseEntity.ok(placed);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================================================
    // TRIAL — save order without payment (dev only)
    // POST /api/payments/trial-place-order
    // ============================================================
    @PostMapping("/trial-place-order")
    public ResponseEntity<?> trialPlaceOrder(
            @Valid @RequestBody VerifyPaymentRequest req,
            Authentication auth) {
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

            OrderResponse placed;
            if ("buy-now".equalsIgnoreCase(req.getMode())) {
                placed = orderService.placeSingleItemOrder(
                        customerId,
                        orderReq,
                        req.getProductId(),
                        req.getSize() != null ? req.getSize() : "One Size",
                        req.getQuantity() != null ? req.getQuantity() : 1
                );
            } else {
                placed = orderService.placeOrderFromCart(customerId, orderReq);
            }

            orderService.updateStatus(placed.getId(), "TRIAL");
            return ResponseEntity.ok(placed);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}