package com.example.diplomasagaorderservice.services;

import com.example.diplomasagaorderservice.models.DTOs.OrderDTO;
import com.example.diplomasagaorderservice.models.DTOs.PaymentDTO;
import com.example.diplomasagaorderservice.models.OrderRepository;
import com.example.diplomasagaorderservice.models.Orders;
import com.example.diplomasagaorderservice.restContollers.responses.OrderResponseDTO;
import com.example.diplomasagaorderservice.restContollers.responses.OrderStatus;
import com.example.diplomasagaorderservice.services.workflow.DeliveryOrderWorkflow;
import com.example.diplomasagaorderservice.services.workflow.StepStatus;
import com.example.diplomasagaorderservice.services.workflow.Workflow;
import com.example.diplomasagaorderservice.services.workflow.WorkflowStep;
import com.example.diplomasagaorderservice.services.workflow.stepsImpl.DeliveryStep;
import com.example.diplomasagaorderservice.services.workflow.stepsImpl.ErrorStep;
import com.example.diplomasagaorderservice.services.workflow.stepsImpl.PaymentStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrchestratorService {


    private OrderRepository orderRepository;

    private RestTemplate restTemplatePayment;

    private RestTemplate restTemplateDelivery;

    @Autowired
    public OrchestratorService(OrderRepository orderRepository, @Qualifier("restPayment") RestTemplate restTemplatePayment, @Qualifier("restDelivery") RestTemplate restTemplateDelivery) {
        this.orderRepository = orderRepository;
        this.restTemplatePayment = restTemplatePayment;
        this.restTemplateDelivery = restTemplateDelivery;
    }

    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        Orders order = orderDTO.toEntity();
        orderRepository.save(order);

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setTargetId(order.getCompanyId());
        paymentDTO.setSourceId(order.getUserId());
        paymentDTO.setPrice(order.getPrice());

        Workflow deliveryOrderWorkflow = getDeliveryOrderWorkflow(order.toOrderDTO(), paymentDTO);

        List<WorkflowStep> steps = deliveryOrderWorkflow.getSteps();

        if (steps.stream().map(WorkflowStep::process).filter(x -> x.equals(Boolean.FALSE)).count() > 0) {
           return revertOrder(deliveryOrderWorkflow);
        }

        return new OrderResponseDTO("Everything is Ok", OrderStatus.COMPLETED);
    }

    private OrderResponseDTO  revertOrder(Workflow workflow) {
        List<WorkflowStep> collect = workflow.getSteps().stream().filter(x -> x.getStepStatus().equals(StepStatus.COMPLETE)).collect(Collectors.toList());
        for (WorkflowStep step: collect) {
            step.revert();
        }

        return new OrderResponseDTO("Order has been cancelled", OrderStatus.CANCELLED);
    }

    public Workflow getDeliveryOrderWorkflow(OrderDTO orderDTO, PaymentDTO paymentDTO) {
        WorkflowStep workflowDelivery = new DeliveryStep(restTemplateDelivery, orderDTO);
        WorkflowStep workflowPayment = new PaymentStep(restTemplatePayment, paymentDTO);
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
