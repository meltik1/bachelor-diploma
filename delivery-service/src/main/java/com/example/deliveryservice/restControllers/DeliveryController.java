package com.example.deliveryservice.restControllers;

import com.example.deliveryservice.model.Couriers;
import com.example.deliveryservice.model.DTOs.OrderDTO;
import com.example.deliveryservice.restControllers.responses.DeliveryResponseDTO;
import com.example.deliveryservice.restControllers.responses.ResponseStatus;
import com.example.deliveryservice.service.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("delivery")
@Slf4j
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @PostMapping
    public Mono<ResponseEntity<DeliveryResponseDTO>> assignCourrier(@RequestBody OrderDTO orderDTO) {
        log.info("Assigning courier to order {}", orderDTO.getId());
        try {
            Couriers couriers = deliveryService.assignCourrier(orderDTO);

            DeliveryResponseDTO deliveryResponseDTO = new DeliveryResponseDTO(couriers.getCourierName(), ResponseStatus.SUCCESS);
            return Mono.just(new ResponseEntity<>(deliveryResponseDTO, HttpStatus.OK));
        }
        catch (Exception exception) {
            DeliveryResponseDTO deliveryResponseDTO = new DeliveryResponseDTO(exception.getMessage(), ResponseStatus.FAILED);
            return Mono.just(new ResponseEntity<>(deliveryResponseDTO, HttpStatus.OK));
        }
    }


    @PostMapping("revert")
    public ResponseEntity<DeliveryResponseDTO> revertAssignCourrier(@RequestBody OrderDTO orderDTO) {
        log.info("Reverting courier to order {}", orderDTO.getId());
        try {
            Couriers couriers = deliveryService.assignCourrier(orderDTO);

            DeliveryResponseDTO deliveryResponseDTO = new DeliveryResponseDTO(couriers.getCourierName(), ResponseStatus.SUCCESS);
            return new ResponseEntity<>(deliveryResponseDTO, HttpStatus.OK);
        }
        catch (Exception exception) {
            DeliveryResponseDTO deliveryResponseDTO = new DeliveryResponseDTO(exception.getMessage(), ResponseStatus.FAILED);
            return new ResponseEntity<>(deliveryResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
