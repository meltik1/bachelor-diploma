package com.example.diplomasagaorderservice.services.workflow;

import reactor.core.publisher.Mono;

public interface WorkflowStep {

    StepStatus getStepStatus();

    Mono<Boolean> process();

    Mono<Boolean> revert();
}
