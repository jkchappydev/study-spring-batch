package io.springbatch.springbatchlecture;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class CustomDecider implements JobExecutionDecider {

    private int count = 1;

    // decider() 는 FlowExecutionStatus 을 반환한다.
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        count++;

        if (count % 2 == 0) {
            return new FlowExecutionStatus("EVEN");
        } else {
            return new FlowExecutionStatus("ODD");
        }
    }

}
