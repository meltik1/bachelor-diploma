package com.example.diplomasagaorderservice.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErlangRequestDTO {

    private String filePath;

    private String lambda;

    private String mu;

    private String time;

    private String percentage;

}
