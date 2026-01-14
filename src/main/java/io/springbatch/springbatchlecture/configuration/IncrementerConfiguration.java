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
public class IncrementerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job incrementerConfigurationJob() {
        return new JobBuilder("incrementerConfigurationJob", jobRepository)
                .start(incrementerConfigurationStep1())
                .next(incrementerConfigurationStep2())
                .incrementer(new CustomJobParametersIncrementer()) // JobParametersIncrementer 직접 구현
                // .incrementer(new RunIdIncrementer()) // Spring Batch 에서 기본으로 제공하는 구현체
                .build();
    }

    @Bean
    public Step incrementerConfigurationStep1() {
        return new StepBuilder("incrementerConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("incrementerConfigurationStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step incrementerConfigurationStep2() {
        return new StepBuilder("incrementerConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("incrementerConfigurationStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
