package com.example.diplomasagaorderservice.services.workflow.stepsImpl;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.models.DTOs.PaymentDTO;
import com.example.diplomasagaorderservice.models.responses.DeliveryResponseDTO;
import com.example.diplomasagaorderservice.models.responses.ResponseStatus;
import com.example.diplomasagaorderservice.services.workflow.StepStatus;
import com.example.diplomasagaorderservice.services.workflow.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class PaymentStep implements WorkflowStep {

    private final RestTemplate restTemplate;

    private PaymentDTO paymentDTO;

    private StepStatus stepStatus = StepStatus.PENDING;

    public PaymentStep(RestTemplate restTemplate, PaymentDTO paymentDTO) {
        this.restTemplate = restTemplate;
        this.paymentDTO = paymentDTO;
    }

    @Override
    public StepStatus getStepStatus() {
        return this.stepStatus;
    }

    @Override
    public Boolean process() {

        ResponseEntity<DeliveryResponseDTO> deliveryResponseDTOResponseEntity = restTemplate.postForEntity("/payment/process", paymentDTO, DeliveryResponseDTO.class);
        boolean xxSuccessful = deliveryResponseDTOResponseEntity.getStatusCode().is2xxSuccessful();
        if (deliveryResponseDTOResponseEntity.getBody().getResponseStatus().equals(ResponseStatus.FAILED)) {
            this.stepStatus = StepStatus.FAILED;
            return Boolean.FALSE;
        }
        this.stepStatus = StepStatus.COMPLETE;
        return Boolean.TRUE;
    }

    @Override
    public Boolean revert() {
        log.warn("Reverting payment");

        ResponseEntity<DeliveryResponseDTO> deliveryResponseDTOResponseEntity = restTemplate.postForEntity("/payment/revert", paymentDTO, DeliveryResponseDTO.class);
        boolean xxSuccessful = deliveryResponseDTOResponseEntity.getStatusCode().is2xxSuccessful();
        if (deliveryResponseDTOResponseEntity.getBody().getResponseStatus().equals(ResponseStatus.FAILED)) {
            this.stepStatus = StepStatus.FAILED;
            return Boolean.FALSE;
        }
        this.stepStatus = StepStatus.COMPLETE;
        return Boolean.TRUE;
    }
}
