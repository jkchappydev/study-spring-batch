package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet1;
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet2;
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet3;
import io.springbatch.springbatchlecture.tasklet.ExecutionContextTasklet4;
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
public class ExecutionContextConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ExecutionContextTasklet1 executionContextTasklet1;
    private final ExecutionContextTasklet2 executionContextTasklet2;
    private final ExecutionContextTasklet3 executionContextTasklet3;
    private final ExecutionContextTasklet4 executionContextTasklet4;

    @Bean
    public Job executionContextJob() {
        return new JobBuilder("executionContextJob", jobRepository)
                .start(executionContextStep1())
                .next(executionContextStep2())
                .next(executionContextStep3())
                .next(executionContextStep4())
                .build();
    }

    @Bean
    public Step executionContextStep1() {
        return new StepBuilder("executionContextStep1", jobRepository)
                .tasklet(executionContextTasklet1, transactionManager)
                .build();
    }

    @Bean
    public Step executionContextStep2() {
        return new StepBuilder("executionContextStep2", jobRepository)
                .tasklet(executionContextTasklet2, transactionManager)
                .build();
    }

    @Bean
    public Step executionContextStep3() {
        return new StepBuilder("executionContextStep3", jobRepository)
                .tasklet(executionContextTasklet3, transactionManager)
                .build();
    }

    @Bean
    public Step executionContextStep4() {
        return new StepBuilder("executionContextStep4", jobRepository)
                .tasklet(executionContextTasklet4, transactionManager)
                .build();
    }

}
