package com.example.ordermanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId; // 訂單項目 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    @NotNull
    private Order order; // 關聯的訂單

    @Column(name = "seller_id", nullable = false)
    @NotNull
    private Long sellerId; // 賣家 ID

    @Column(name = "item_id", nullable = false)
    @NotNull
    private Long itemId; // 商品 ID

    @Column(name = "item_name", nullable = false)
    @NotNull
    @Size(max = 255)
    private String itemName; // 商品名稱

    @Column(name = "item_quantity", nullable = false)
    @NotNull
    @Positive
    private Integer itemQuantity; // 商品數量

    @Column(name = "item_price", nullable = false)
    @NotNull
    @Positive
    private BigDecimal itemPrice; // 商品單價

    @Column(name = "item_size", nullable = false)
    @NotNull
    @Size(max = 50)
    private String itemSize; // 商品尺寸

    // Constructors
    public OrderItem() {
    }

    // Getters and Setters

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (order != null && !order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    // equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        return Objects.equals(orderItemId, orderItem.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderItemId);
    }
}
