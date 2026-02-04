package io.springbatch.springbatchlecture.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobAndStepStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        System.out.println("stepName: " + stepName);
        stepExecution.getExecutionContext().put("name", "user1"); // 공유하고자 하는 데이터 설정 가능
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus exitStatus = stepExecution.getExitStatus(); // 현재 Step 의 상태
        System.out.println("ExitStatus: " + exitStatus);
        BatchStatus batchStatus = stepExecution.getStatus(); // 배치 상태
        System.out.println("BatchStatus : " + batchStatus);
        String name = (String) stepExecution.getExecutionContext().get("name");
        System.out.println("name: " + name);

        return ExitStatus.COMPLETED; // ExitStatus를 COMPLETED 로 "유도"할 수는 있지만, Step 실패(BatchStatus/FAILED)를 성공으로 바꾸는 건 불가능하다.
    }

}
