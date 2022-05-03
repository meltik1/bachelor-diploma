package com.example.paymentservice.service;

import com.example.paymentservice.model.Balance;
import com.example.paymentservice.model.Payments;
import com.example.paymentservice.repositories.BalancesRepository;
import com.example.paymentservice.repositories.PaymentsRepositories;
import com.example.paymentservice.restControllers.requests.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class PaymentService {

    BalancesRepository balancesRepository;

    PaymentsRepositories paymentsRepositories;

    @Autowired
    public PaymentService(BalancesRepository balancesRepository, PaymentsRepositories paymentsRepositories) {
        this.balancesRepository = balancesRepository;
        this.paymentsRepositories = paymentsRepositories;
    }

    public void processPayment(PaymentDTO paymentDTO) {
        Balance balanceTarget = balancesRepository.findById(paymentDTO.getTargetId()).orElseThrow(EntityNotFoundException::new);
        Double balanceTargetSum = balanceTarget.getBalance();

        Balance balanceSource = balancesRepository.findById(paymentDTO.getSourceId()).orElseThrow(EntityNotFoundException::new);
        Double balanceSourceSum = balanceSource.getBalance();

        if (balanceSourceSum < paymentDTO.getPrice()) {
            throw new IllegalArgumentException("Not enough money");
        }

        balanceTarget.setBalance(balanceTargetSum + paymentDTO.getPrice());
        balanceSource.setBalance(balanceSourceSum - paymentDTO.getPrice());
        balancesRepository.save(balanceSource);
        balancesRepository.save(balanceTarget);

        Payments payments = new Payments();
        payments.setPrice(paymentDTO.getPrice());
        payments.setSource(balanceSource);
        payments.setTarget(balanceTarget);
        paymentsRepositories.save(payments);
    }


    public void revertPayment(PaymentDTO paymentDTO) {
        Balance balanceTarget = balancesRepository.findById(paymentDTO.getTargetId()).orElseThrow(EntityNotFoundException::new);
        Double balanceTargetSum = balanceTarget.getBalance();

        Balance balanceSource = balancesRepository.findById(paymentDTO.getSourceId()).orElseThrow(EntityNotFoundException::new);
        Double balanceSourceSum = balanceSource.getBalance();

        if (balanceSourceSum < paymentDTO.getPrice()) {
            throw new IllegalArgumentException("Not enough money");
        }

        balanceTarget.setBalance(balanceTargetSum - paymentDTO.getPrice());
        balanceSource.setBalance(balanceSourceSum + paymentDTO.getPrice());
        balancesRepository.save(balanceSource);
        balancesRepository.save(balanceTarget);

        Payments payments = new Payments();
        payments.setPrice(paymentDTO.getPrice());
        payments.setSource(balanceTarget);
        payments.setTarget(balanceSource);
        paymentsRepositories.save(payments);
    }



}
