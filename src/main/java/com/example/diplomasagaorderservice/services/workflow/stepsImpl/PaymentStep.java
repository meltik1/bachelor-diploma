package com.example.diplomasagaorderservice.services.workflow.stepsImpl;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.models.DTOs.PaymentDTO;
import com.example.diplomasagaorderservice.models.responses.DeliveryResponseDTO;
import com.example.diplomasagaorderservice.models.responses.PaymentResponse;
import com.example.diplomasagaorderservice.models.responses.ResponseStatus;
import com.example.diplomasagaorderservice.services.workflow.StepStatus;
import com.example.diplomasagaorderservice.services.workflow.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class PaymentStep implements WorkflowStep {

    private final WebClient webClient;


    private PaymentDTO paymentDTO;

    private StepStatus stepStatus = StepStatus.PENDING;

    public PaymentStep(WebClient webClient, PaymentDTO paymentDTO) {
        this.paymentDTO = paymentDTO;
        this.webClient = webClient;
    }

    @Override
    public StepStatus getStepStatus() {
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        return  this.webClient.post().uri("/payment/process").
                body(BodyInserters.fromValue(paymentDTO))
                .retrieve()
                .bodyToMono(PaymentResponse.class).
                map( x -> x.getResponseStatus().equals(ResponseStatus.SUCCESS))
                .doOnNext(b -> this.stepStatus = b ? StepStatus.COMPLETE : StepStatus.FAILED);
//         ResponseEntity<DeliveryResponseDTO> deliveryResponseDTOResponseEntity = restTemplate.postForEntity("/payment/process", paymentDTO, DeliveryResponseDTO.class);
//        boolean xxSuccessful = deliveryResponseDTOResponseEntity.getStatusCode().is2xxSuccessful();
//        if (deliveryResponseDTOResponseEntity.getBody().getResponseStatus().equals(ResponseStatus.FAILED)) {
//            this.stepStatus = StepStatus.FAILED;
//            return Boolean.FALSE;
//        }
//        this.stepStatus = StepStatus.COMPLETE;
//        return Boolean.TRUE;
    }
    @Override
    public Mono<Boolean> revert() {

        log.warn("Reverting payment");
        return  this.webClient.post().uri("/payment/revert").
                body(BodyInserters.fromValue(paymentDTO))
                .retrieve()
                .bodyToMono(PaymentResponse.class).
                 map( x -> true).
                onErrorReturn(false);

    }
}
