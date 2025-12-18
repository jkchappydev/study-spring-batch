package io.springbatch.springbatchlecture.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet4 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("executionContextStep4");

        // Step3 에서 저장한 "name" 이 Step4 에서 확인할 수 있는지 확인한다.
        // Step3 에서 실패한 Step 를 재시작해서 성공하면, DB 에 있는 ExecutionContext 에 있는 해당 값을 가지고와서 출력해야 한다.
        System.out.println("name : " + chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name")); // name : user1

        return RepeatStatus.FINISHED;
    }

}
