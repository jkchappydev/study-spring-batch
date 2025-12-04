package io.springbatch.springbatchlecture;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobExecutionConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job executionJob() {
        return new JobBuilder("executionJob", jobRepository)
                .start(executionStep1())
                .next(executionStep2())
                .build();
    }

    @Bean
    public Step executionStep1() {
        return new StepBuilder("executionStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("executionStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step executionStep2() {
        return new StepBuilder("executionStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("executionStep2 was executed");
                    // throw new RuntimeException("executionStep2 has failed"); // 강제 실패 발생 (BatchStatus: failed, 현재 JobParameters 는 "user1")
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
