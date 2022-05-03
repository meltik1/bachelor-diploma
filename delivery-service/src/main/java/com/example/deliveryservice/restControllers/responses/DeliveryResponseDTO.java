package com.example.deliveryservice.restControllers.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponseDTO {
    private String message;


    private ResponseStatus responseStatus;
}
