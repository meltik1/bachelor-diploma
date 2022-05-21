package com.example.diplomasagaorderservice.restContollers.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatisticsResponseDTO {

    private Double requestsIntensivity;

    private Double averageProcessing;

    private Double averageTimeInQueue;

    private Integer channelsNumber;
}
