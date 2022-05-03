package com.example.diplomasagaorderservice.services.workflow.stepsImpl;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.models.responses.DeliveryResponseDTO;
import com.example.diplomasagaorderservice.models.responses.ResponseStatus;
import com.example.diplomasagaorderservice.services.workflow.StepStatus;
import com.example.diplomasagaorderservice.services.workflow.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DeliveryStep implements WorkflowStep {

    private final RestTemplate restTemplate;

    private OrderDTO orderDTO;

    private StepStatus stepStatus = StepStatus.PENDING;

    public DeliveryStep(RestTemplate restTemplate, OrderDTO orderDTO) {
        this.restTemplate = restTemplate;
        this.orderDTO = orderDTO;
    }

    @Override
    public StepStatus getStepStatus() {
        return stepStatus;
    }

    @Override
    public Boolean process() {
        ResponseEntity<DeliveryResponseDTO> deliveryResponseDTOResponseEntity = restTemplate.postForEntity("/delivery", orderDTO, DeliveryResponseDTO.class);
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
        log.warn("Reverting courier assignment");

        ResponseEntity<DeliveryResponseDTO> deliveryResponseDTOResponseEntity = restTemplate.postForEntity("/delivery/revert", orderDTO, DeliveryResponseDTO.class);
        boolean xxSuccessful = deliveryResponseDTOResponseEntity.getStatusCode().is2xxSuccessful();
        if (deliveryResponseDTOResponseEntity.getBody().getResponseStatus().equals(ResponseStatus.FAILED)) {
            this.stepStatus = StepStatus.FAILED;
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
