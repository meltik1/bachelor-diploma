package com.example.diplomasagaorderservice.services.workflow.stepsImpl;

import com.example.diplomasagaorderservice.services.workflow.StepStatus;
import com.example.diplomasagaorderservice.services.workflow.WorkflowStep;
import reactor.core.publisher.Mono;

public class ErrorStep implements WorkflowStep {

    private  StepStatus stepStatus = StepStatus.PENDING;

    @Override
    public StepStatus getStepStatus() {
        return stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        this.stepStatus = StepStatus.FAILED;
        return Mono.just(Boolean.FALSE);
    }

    @Override
    public Mono<Boolean> revert() {
        this.stepStatus = StepStatus.COMPLETE;
        return Mono.just(Boolean.TRUE);
    }
}
