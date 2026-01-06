package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobRepositoryListener implements JobExecutionListener {

    private final JobRepository jobRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // BatchConfigurer 인터페이스 또는 해당 인터페이스를 구현한 구현체를 직접 생성해서 설정을 커스터마이징 할 수도 있다.
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Job 이 다 수행이 되고 난 이후에 JobExecution 에 마지막으로 저장된 정보를 가지고 온다.
        String jobName = jobExecution.getJobInstance().getJobName();

        // BATCH_JOB_EXECUTION 테이블에 이미 저장되어 있는 jobParameters 를 제공해야 해당 jobParameters 에 해당하는 jobExecution 을 가지고 올 수 있다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "20251218")
                .toJobParameters(); // BATCH_JOB_PARAMS 의 KEY_NAME, STRING_VAL 컬럼

        JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);
        if (lastJobExecution != null) {
            // JobExecution 이 포함하고 있는 Step 들의 StepExecution 목록을 출력
            for (StepExecution stepExecution : lastJobExecution.getStepExecutions()) {
                // 현재 배치의 상태를 가지고 온다.
                BatchStatus status = stepExecution.getStatus();
                System.out.println("status: " + status);
                ExitStatus exitStatus = stepExecution.getExitStatus();
                System.out.println("exitStatus: " + exitStatus);
                String stepName = stepExecution.getStepName();
                System.out.println("stepName: " + stepName);
            }
        }
    }

}
