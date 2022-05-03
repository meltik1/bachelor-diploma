package com.example.diplomasagaorderservice.services.workflow.stepsImpl;

import com.example.diplomasagaorderservice.services.workflow.StepStatus;
import com.example.diplomasagaorderservice.services.workflow.WorkflowStep;

public class ErrorStep implements WorkflowStep {

    private  StepStatus stepStatus = StepStatus.PENDING;

    @Override
    public StepStatus getStepStatus() {
        return stepStatus;
    }

    @Override
    public Boolean process() {
        this.stepStatus = StepStatus.FAILED;
        return Boolean.FALSE;
    }

    @Override
    public Boolean revert() {
        this.stepStatus = StepStatus.COMPLETE;
        return Boolean.TRUE;
    }
}
