package com.example.diplomasagaorderservice.restContollers;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.restContollers.responses.OrderResponseDTO;
import com.example.diplomasagaorderservice.restContollers.responses.StatisticsResponseDTO;
import com.example.diplomasagaorderservice.services.OrchestratorService;
import com.example.diplomasagaorderservice.services.StatisticsService;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderDTO orderDTO) {

        Instant start = Instant.now();
        OrderResponseDTO order = orchestratorService.createOrder(orderDTO);
        Instant finish = Instant.now();
        statisticsService.addMeasuremnt(Duration.between(start, finish).toMillis());
        return new ResponseEntity<>(order,HttpStatus.OK);
    }

    @GetMapping("statistics")
    public ResponseEntity<StatisticsResponseDTO> getStatistics() throws IOException {
        StatisticsResponseDTO statistics = statisticsService.getStatistics();
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}
