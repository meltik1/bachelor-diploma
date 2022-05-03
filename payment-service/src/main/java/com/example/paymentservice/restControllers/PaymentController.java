package com.example.paymentservice.restControllers;

import com.example.paymentservice.restControllers.requests.PaymentDTO;
import com.example.paymentservice.restControllers.responses.PaymentResponse;
import com.example.paymentservice.restControllers.responses.ResponseStatus;
import com.example.paymentservice.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
@Slf4j
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> addToBalance(@RequestBody PaymentDTO paymentDTO) {
        log.info("Adding to balances  sourceId: {}  targetId : {}  price: {}", paymentDTO.getSourceId(), paymentDTO.getTargetId(), paymentDTO.getPrice());
        try {
            paymentService.processPayment(paymentDTO);
            PaymentResponse paymentResponse = new PaymentResponse("Ok", ResponseStatus.SUCCESS);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        }
        catch (Exception exception) {
            PaymentResponse paymentResponse = new PaymentResponse(exception.getMessage(), ResponseStatus.SUCCESS);
            return new ResponseEntity<>(paymentResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/revert")
    public ResponseEntity<PaymentResponse> revertBalance(@RequestBody PaymentDTO paymentDTO) {
        log.info("Reverting balances  sourceId: {}  targetId : {}  price: {}", paymentDTO.getSourceId(), paymentDTO.getTargetId(), paymentDTO.getPrice());
        try {
            paymentService.revertPayment(paymentDTO);
            PaymentResponse paymentResponse = new PaymentResponse("Ok", ResponseStatus.SUCCESS);
            return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
        }
        catch (Exception exception) {
            PaymentResponse paymentResponse = new PaymentResponse(exception.getMessage(), ResponseStatus.FAILED);
            return new ResponseEntity<>(paymentResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
