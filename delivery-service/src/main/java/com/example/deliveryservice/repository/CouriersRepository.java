package com.example.deliveryservice.repository;

import com.example.deliveryservice.model.Couriers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouriersRepository extends JpaRepository<Couriers, Long> {

        //List<Couriers> findAllByCouriersStatus_Free();
}
