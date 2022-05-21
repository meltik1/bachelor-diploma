package com.example.diplomasagaorderservice.restContollers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private String message;
    OrderStatus orderStatus;
    private BigInteger startTime;
    private BigInteger startProcessingTime;
}
