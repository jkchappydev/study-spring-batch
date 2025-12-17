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
public class StepExecutionConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepExecutionConfigurationJob() {
        return new JobBuilder("stepExecutionConfigurationJob", jobRepository)
                .start(stepExecutionConfigurationStep1())
                .next(stepExecutionConfigurationStep2())
                .next(stepExecutionConfigurationStep3())
                .build();
    }

    @Bean
    public Step stepExecutionConfigurationStep1() {
        return new StepBuilder("stepExecutionConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepExecutionConfigurationStep1");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepExecutionConfigurationStep2() {
        return new StepBuilder("stepExecutionConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepExecutionConfigurationStep2");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepExecutionConfigurationStep3() {
        return new StepBuilder("stepExecutionConfigurationStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepExecutionConfigurationStep3");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
