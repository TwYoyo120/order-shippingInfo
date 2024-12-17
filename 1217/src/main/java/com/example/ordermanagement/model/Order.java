package com.example.ordermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId; // 訂單 ID

    @Column(name = "order_status", nullable = false)
    @NotNull
    private String orderStatus; // 訂單狀態

    @Column(name = "order_total", nullable = false)
    @NotNull
    @PositiveOrZero
    private BigDecimal orderTotal = BigDecimal.ZERO; // 訂單總金額

    @Column(name = "buyer_id", nullable = false)
    @NotNull
    private Long buyerId; // 買家 ID

    @Column(name = "order_date", nullable = false)
    @NotNull
    private LocalDateTime orderDate; // 訂單日期

    @Column(name = "payment_status", nullable = false)
    @NotNull
    private String paymentStatus = "Unpaid"; // 付款狀態

    @Column(name = "shipping_status", nullable = false)
    @NotNull
    private String shippingStatus = "Not Shipped"; // 運輸狀態

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>(); // 訂單項目列表

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ShippingInfo> shippingInfos = new ArrayList<>(); // 物流信息列表

    // Constructors
    public Order() {
    }

    // Business Logic Methods
    public void recalculateOrderTotal() {
        this.orderTotal = orderItems.stream()
                .map(item -> item.getItemPrice().multiply(new BigDecimal(item.getItemQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addOrderItem(OrderItem item) {
        boolean exists = this.orderItems.stream()
                .anyMatch(existingItem -> existingItem.getItemId().equals(item.getItemId()));
        if (!exists) {
            item.setOrder(this);
            this.orderItems.add(item);
            recalculateOrderTotal();
        }
    }

    public void removeOrderItem(OrderItem item) {
        this.orderItems.remove(item);
        recalculateOrderTotal();
    }

    public void addShippingInfo(ShippingInfo shippingInfo) {
        shippingInfo.setOrder(this);
        this.shippingInfos.add(shippingInfo);
    }

    public void removeShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfos.remove(shippingInfo);
        shippingInfo.setOrder(null);
    }

    // Getters and Setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<ShippingInfo> getShippingInfos() {
        return shippingInfos;
    }

    public void setShippingInfos(List<ShippingInfo> shippingInfos) {
        this.shippingInfos = shippingInfos;
    }

    // equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
