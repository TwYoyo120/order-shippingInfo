package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // 按買家 ID 查詢訂單
    List<Order> findByBuyerId(Long buyerId);

    // 按訂單 ID 查詢訂單
    Optional<Order> findByOrderId(Long orderId);

    // 按訂單狀態查詢訂單
    List<Order> findByOrderStatus(String orderStatus);
}
