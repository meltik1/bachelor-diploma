package com.example.paymentservice.repositories;

import com.example.paymentservice.model.Balance;
import com.example.paymentservice.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepositories  extends JpaRepository<Payments, Long> {
}
