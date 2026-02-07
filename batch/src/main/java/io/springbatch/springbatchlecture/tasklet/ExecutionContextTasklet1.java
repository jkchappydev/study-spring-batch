package io.springbatch.springbatchlecture.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet1 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("executionContextStep1");

        // StepExecution 안에 있는 JobExecution 를 가져와서, JobExecution 에 있는 ExecutionContext 를 가져온다.
        ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();

        // StepExecution 에서 ExecutionContext 를 가져온다.
        ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

        // StepExecution 안에 있는 JobExecution 에서, JobInstance 에 있는 jobName 을 가져온다. (이 때, contribution 이 아닌, chunkContext 참조)
        String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName(); // executionContextJob

        // StepExecution 에서, stepName 을 가져온다. (이 때, contribution 이 아닌, chunkContext 참조)
        String stepName = chunkContext.getStepContext().getStepExecution().getStepName(); // executionContextStep1

        // jobExecutionContext 에 jobName 가 없으면
        if (jobExecutionContext.get("jobName") == null) { // null
            jobExecutionContext.put("jobName", jobName); // 위에서 가져온 jobName 을 jobExecutionContext 에 넣는다.
        }

        // stepExecutionContext 에 stepName 가 없으면
        if (stepExecutionContext.get("stepName") == null) { // null
            stepExecutionContext.put("stepName", stepName); // 위에서 가져온 stepName 을 stepExecutionContext 에 넣는다.
        }

        System.out.println("jobName : " + jobExecutionContext.get("jobName")); // jobName : executionContextJob
        System.out.println("stepName : " + stepExecutionContext.get("stepName")); // stepName : executionContextStep1

        return RepeatStatus.FINISHED;
    }

}