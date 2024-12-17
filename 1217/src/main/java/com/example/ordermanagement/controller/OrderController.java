package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 映射到訂單管理頁面
    @GetMapping("/order-management")
    public String adminOrderManagement() {
        return "adminOrderManagement"; 
    }

    @GetMapping("/memberPurchase")
    public String memberPurchase() {
        return "memberPurchase";
    }

    // 建立新訂單（POST）
    @ResponseBody
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.ok(created);
    }

    // 獲取訂單列表，支持多種篩選條件和分頁，並返回符合前端需求的結構
    @ResponseBody
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var result = orderService.getOrdersByFilters(buyerId, sellerId, orderId, orderStatus, startDate, endDate, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(Map.of(
            "content", result.getContent(),
            "totalPages", result.getTotalPages(),
            "currentPage", result.getNumber() + 1
        ));
    }

    // 根據訂單ID獲取訂單詳情
    @ResponseBody
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        return orderOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 更新訂單詳情(以訂單ID)
    @ResponseBody
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrderDetails(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> updatedFields) {
        try {
            boolean updated = orderService.updateOrderDetails(
                    orderId,
                    (String) updatedFields.get("paymentStatus"),
                    (String) updatedFields.get("shippingStatus"),
                    (String) updatedFields.get("orderStatus")
            );
            return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 棄單功能(以訂單ID)
    @ResponseBody
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            boolean canceled = orderService.cancelOrder(orderId);
            return canceled ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
