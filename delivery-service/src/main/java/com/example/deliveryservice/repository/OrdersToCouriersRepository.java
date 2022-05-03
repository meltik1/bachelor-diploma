package com.example.deliveryservice.repository;

import com.example.deliveryservice.model.Couriers;
import com.example.deliveryservice.model.Orders;
import com.example.deliveryservice.model.OrdersToCuriers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersToCouriersRepository extends JpaRepository<OrdersToCuriers, Long> {
    Optional<OrdersToCuriers> findOrdersToCuriersByOrders(Orders orders);
}
