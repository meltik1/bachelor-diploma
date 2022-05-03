package com.example.paymentservice.repositories;

import com.example.paymentservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalancesRepository extends JpaRepository<Balance, Long> {

}
