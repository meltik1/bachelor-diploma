package com.example.diplomasagaorderservice.services;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.models.DTOs.PaymentDTO;
import com.example.diplomasagaorderservice.models.OrderRepository;
import com.example.diplomasagaorderservice.models.Orders;
import com.example.diplomasagaorderservice.restContollers.responses.OrderResponseDTO;
import com.example.diplomasagaorderservice.restContollers.responses.OrderStatus;
import com.example.diplomasagaorderservice.services.workflow.*;
import com.example.diplomasagaorderservice.services.workflow.stepsImpl.DeliveryStep;
import com.example.diplomasagaorderservice.services.workflow.stepsImpl.ErrorStep;
import com.example.diplomasagaorderservice.services.workflow.stepsImpl.PaymentStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrchestratorService {


    private OrderRepository orderRepository;

        private WebClient webTemplatePayment;

        private WebClient webTemplateDelivery;

    @Autowired
    public OrchestratorService(OrderRepository orderRepository, @Qualifier("payment") WebClient webClient1, @Qualifier("delivery") WebClient webClient2) {
        this.orderRepository = orderRepository;
        this.webTemplatePayment = webClient1;
        this.webTemplateDelivery = webClient2;
    }

    public Mono<OrderResponseDTO> createOrder(OrderDTO orderDTO) {
        Orders order = orderDTO.toEntity();
        orderRepository.save(order);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setTargetId(order.getCompanyId());
        paymentDTO.setSourceId(order.getUserId());
        paymentDTO.setPrice(order.getPrice());

        Workflow deliveryOrderWorkflow = getDeliveryOrderWorkflow(order.toOrderDTO(), paymentDTO);
        return Flux.fromStream(() -> deliveryOrderWorkflow.getSteps().stream())
                .flatMap(WorkflowStep::process)
                .handle(((aBoolean, objectSynchronousSink) -> {
                    if (aBoolean)
                        objectSynchronousSink.next(true);
                    else
                        objectSynchronousSink.error(new WorkflowException("Order creation failed"));
                }))
                .then(Mono.fromCallable(()-> getResponseDTO("Order complete succesfully", OrderStatus.COMPLETED)))
                .onErrorResume(ex -> this.revertOrder(deliveryOrderWorkflow));
//        List<WorkflowStep> steps = deliveryOrderWorkflow.getSteps();
//
//        if (steps.stream().map(WorkflowStep::process).filter(x -> x.equals(Boolean.FALSE)).count() > 0) {
//           return revertOrder(deliveryOrderWorkflow);
//        }
//
//        return new OrderResponseDTO("Everything is Ok", OrderStatus.COMPLETED);
    }

    private Mono<OrderResponseDTO>  revertOrder(Workflow workflow) {
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .filter(x -> x.getStepStatus().equals(StepStatus.COMPLETE))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(this.getResponseDTO("Order canceleed", OrderStatus.CANCELLED)));
//        List<WorkflowStep> collect = workflow.getSteps().stream().filter(x -> x.getStepStatus().equals(StepStatus.COMPLETE)).collect(Collectors.toList());
//        for (WorkflowStep step: collect) {
//            step.revert();
//        }
//
//        return new OrderResponseDTO("Order has been cancelled", OrderStatus.CANCELLED);
    }

    private OrderResponseDTO getResponseDTO(String message, OrderStatus status){
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setMessage(message);
        orderResponseDTO.setOrderStatus(status);
        return orderResponseDTO;
    }
    public Workflow getDeliveryOrderWorkflow(OrderDTO orderDTO, PaymentDTO paymentDTO) {
        WorkflowStep workflowDelivery = new DeliveryStep(webTemplateDelivery, orderDTO);
        WorkflowStep workflowPayment = new PaymentStep(webTemplatePayment, paymentDTO);
        Random random = new Random();
        Integer step = random.nextInt(3);
        WorkflowStep workflowError = new ErrorStep();

        if (step ==  0) {
            log.info("Workflow with error will be executed");
            return new DeliveryOrderWorkflow(Arrays.asList(workflowDelivery, workflowPayment, workflowError));
        }
        else {
            log.info("Workflow without error will be executed");
            Workflow workflow = new DeliveryOrderWorkflow(Arrays.asList(workflowDelivery, workflowPayment));
            return workflow;
        }
    }
}
