package com.example.diplomasagaorderservice.restContollers;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.restContollers.responses.StatisticsResponseDTO;
import com.example.diplomasagaorderservice.services.OrchestratorService;
import com.example.diplomasagaorderservice.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("order")
public class OrchestratorContoller {

    @Autowired
    OrchestratorService orchestratorService;

    @Autowired
    StatisticsService statisticsService;


    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody OrderDTO orderDTO) {

        Instant start = Instant.now();
        orchestratorService.createOrder(orderDTO);
        Instant finish = Instant.now();
        statisticsService.addMeasuremnt(Duration.between(start, finish).toMillis());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("statistics")
    public ResponseEntity<StatisticsResponseDTO> getStatistics() {
        StatisticsResponseDTO statistics = statisticsService.getStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}