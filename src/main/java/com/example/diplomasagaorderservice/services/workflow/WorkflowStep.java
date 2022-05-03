package com.example.diplomasagaorderservice.services.workflow;

public interface WorkflowStep {

    StepStatus getStepStatus();

    Boolean process();

    Boolean revert();
}
