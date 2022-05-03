package com.example.deliveryservice.repository;

import com.example.deliveryservice.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long>{
}
