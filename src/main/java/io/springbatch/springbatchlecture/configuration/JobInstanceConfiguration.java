package io.springbatch.springbatchlecture.configuration;

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
public class JobInstanceConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job instanceJob() {
        return new JobBuilder("instanceJob", jobRepository)
                .start(instanceStep1())
                .next(instanceStep2())
                .build();
    }

    @Bean
    public Step instanceStep1() {
        return new StepBuilder("instanceStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("instanceStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step instanceStep2() {
        return new StepBuilder("instanceStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("instanceStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
