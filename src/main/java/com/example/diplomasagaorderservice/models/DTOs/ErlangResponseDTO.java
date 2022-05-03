package com.example.diplomasagaorderservice.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErlangResponseDTO {

    private Integer channelsNumber;

    private Double percentage;
}
