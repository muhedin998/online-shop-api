package com.example.online_shop.order.repository;

import com.example.online_shop.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    @Deprecated
    default List<Order> findByUserId(Long userId) {
        return findByUserIdOrderByOrderDateDesc(userId);
    }
}
