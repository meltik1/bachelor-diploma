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
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("order")
public class OrchestratorContoller {

    @Autowired
    OrchestratorService orchestratorService;

    @Autowired
    StatisticsService statisticsService;


    @PostMapping
    public Mono<OrderResponseDTO> createOrder(@RequestBody OrderDTO orderDTO) {

        Date startProcessingTime = new Date();
        Instant start = Instant.now();
        Mono<OrderResponseDTO> order = orchestratorService.createOrder(orderDTO);
        Instant finish = Instant.now();
        statisticsService.addMeasuremnt(Duration.between(start, finish).toMillis(), startProcessingTime.getTime() - orderDTO.getStartTime());
        return  order;
    }

    @GetMapping("statistics")
    public Mono<ResponseEntity<StatisticsResponseDTO>> getStatistics() throws IOException {
        StatisticsResponseDTO statistics = statisticsService.getStatistics();
        return Mono.just(new ResponseEntity<>(statistics, HttpStatus.OK));
    }

    @GetMapping("reset")
    public  ResponseEntity<Void> resetTime() {
        statisticsService.resetTime();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
