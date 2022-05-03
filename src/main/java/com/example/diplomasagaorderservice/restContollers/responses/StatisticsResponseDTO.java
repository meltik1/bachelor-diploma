package com.example.diplomasagaorderservice.restContollers.responses;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponseDTO {

    private Integer requestsNumber;

    private Double averageProcessing;
}
