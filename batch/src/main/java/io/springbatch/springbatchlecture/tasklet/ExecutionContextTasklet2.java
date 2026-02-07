package io.springbatch.springbatchlecture.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet2 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("executionContextStep2");

        ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();

        // Step1이 정상 종료됐으면 해당하는 ExecutionContext 값들이 DB에 저장돼 있을 거고,
        // Step2 실행 시점엔 jobExecutionContext 는 Job 단위로 공유되니까 jobName 을 확인 가능하다.
        // 하지만, stepExecutionContext 는 Step 마다 개별 ExecutionContext 라서 공유가 불가능하기 때문에 Step1에서 넣은 stepName 은 여기서 확인 불가능하다.
        System.out.println("jobName : " + jobExecutionContext.get("jobName")); // jobName : executionContextJob
        System.out.println("stepName : " + stepExecutionContext.get("stepName")); // stepName : null (Step 간에는 ExecutionContext 를 공유하지 않기 때문에, Step1 에서 넣은 stepName 이 확인이 안되서 null 이다.)

        String stepName = chunkContext.getStepContext().getStepExecution().getStepName();
        if (stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.put("stepName", stepName);
        }

        return RepeatStatus.FINISHED;
    }

}
