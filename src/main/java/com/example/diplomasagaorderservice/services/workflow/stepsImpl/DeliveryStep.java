package com.example.diplomasagaorderservice.services.workflow.stepsImpl;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
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
public class DeliveryStep implements WorkflowStep {




    private final WebClient webClient;
    private OrderDTO orderDTO;

    private StepStatus stepStatus = StepStatus.PENDING;

    public DeliveryStep( WebClient webClient, OrderDTO orderDTO) {
        this.orderDTO = orderDTO;
        this.webClient = webClient;
    }

    @Override
    public StepStatus getStepStatus() {
        return stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        return  this.webClient.post().uri("/delivery").
                body(BodyInserters.fromValue(orderDTO))
                .retrieve()
                .bodyToMono(DeliveryResponseDTO.class).
                map( x -> x.getResponseStatus().equals(ResponseStatus.SUCCESS))
                .doOnNext(b -> this.stepStatus = b ? StepStatus.COMPLETE : StepStatus.FAILED);

    }

    @Override
    public Mono<Boolean> revert() {
        log.warn("Reverting courier assignment");
        return this.webClient.post().uri("/delivery/revert").
                body(BodyInserters.fromValue(orderDTO))
                .retrieve()
                .bodyToMono(DeliveryResponseDTO.class).
                map(x -> x.getResponseStatus().equals(ResponseStatus.SUCCESS))
                .doOnNext(b -> this.stepStatus = b ? StepStatus.COMPLETE : StepStatus.FAILED);
    }
}
