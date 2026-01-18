package io.springbatch.springbatchlecture.configuration;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class PassCheckingListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String exitCode = stepExecution.getExitStatus().getExitCode();

        // Step 의 ExitStatus 가 FAILED 가 아니면, ExitStatus 를 PASS 로 설정한다.
        // FAILED 면 null 을 리턴해서 기존 FAILED ExitStatus 를 그대로 유지한다.
        if(!exitCode.equals(ExitStatus.FAILED.getExitCode())) {
            return new ExitStatus("PASS");
        }

        return null;
    }

}
