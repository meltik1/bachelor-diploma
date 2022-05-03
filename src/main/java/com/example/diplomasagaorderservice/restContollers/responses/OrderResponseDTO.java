package com.example.diplomasagaorderservice.restContollers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private String message;
    OrderStatus orderStatus;
}
