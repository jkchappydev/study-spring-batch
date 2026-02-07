package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TaskletStepArchitectureConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job taskletStepArchitectureJob() {
        return new JobBuilder("taskletStepArchitectureJob", jobRepository)
                .start(taskletStepArchitectureStep1())
                .next(taskletStepArchitectureStep2())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        // Job 실행 전 수행할 로직 작성
                        System.out.println("Job 실행 전");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        // Job 실행 후 수행할 로직 작성
                        System.out.println("Job 실행 후");
                    }
                })
                .build();
    }

    @Bean
    public Step taskletStepArchitectureStep1() {
        return new StepBuilder("taskletStepArchitectureStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("taskletStepArchitectureStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        // Step 실행 전 수행할 로직 작성
                        System.out.println("Step 실행 전");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        // Step 실행 후 수행할 로직 작성
                        // 필요하면 ExitStatus 를 커스텀할 수 있다 (주의: 여기서 값을 반환하면 기존 ExitStatus 를 덮어써서 흐름/결과에 영향을 줄 수 있음)
                        System.out.println("Step 실행 후");
                        return ExitStatus.COMPLETED;
                    }
                })
                .build();
    }

    @Bean
    public Step taskletStepArchitectureStep2() {
        return new StepBuilder("taskletStepArchitectureStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("taskletStepArchitectureStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
