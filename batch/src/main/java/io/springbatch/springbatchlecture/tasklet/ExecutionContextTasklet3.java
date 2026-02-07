package io.springbatch.springbatchlecture.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class ExecutionContextTasklet3 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("executionContextStep3");

        // Step3에서 강제적으로 예외를 발생 시키고, Job 을 FAILED 상태로 만든다.
        // 이 후 Job 을 재시작하면, 실패한 지점(Step3)에서 넣어둔 ExecutionContext 값이 그대로 남아있는지 확인한다.
        // 재시작 후 Step3가 정상적으로 끝나면, 이전에 저장해둔 데이터(name)를 다시 활용할 수 있는 지 확인한다.
        Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");

        // 처음 실행할 땐 ExecutionContext 에 name 이 없어서(null) 여기로 들어온다.
        // 그래서 name 을 한 번 넣어두고 일부러 예외를 발생시켜 Step3를 실패 처리한다.
        // 근데 재시작하면 이전에 저장해둔 name 이 남아있어서 null 이 아니고,
        // 따라서 예외를 발생시키지 않고 정상 흐름으로 그대로 진행된다.
        if (name == null) { // 첫번 째 실행 (name == null), 두번째 실행 (name == user1)
            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("name", "user1");
            throw new RuntimeException("step3 was failed");
        }

        return RepeatStatus.FINISHED;
    }

}
