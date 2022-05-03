package com.example.diplomasagaorderservice.services.workflow;

import java.util.List;

public class DeliveryOrderWorkflow  implements Workflow{
    private List<WorkflowStep> workflowSteps;

    public DeliveryOrderWorkflow(List<WorkflowStep> workflowSteps) {
        this.workflowSteps = workflowSteps;
    }

    @Override
    public List<WorkflowStep> getSteps() {
        return workflowSteps;
    }
}
