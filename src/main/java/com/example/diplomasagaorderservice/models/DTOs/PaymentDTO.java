package com.example.diplomasagaorderservice.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long sourceId;

    private Long targetId;

    private Double price;


}
