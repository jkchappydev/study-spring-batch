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
public class StepContributionConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepContributionConfigurationJob() {
        return new JobBuilder("stepContributionConfigurationJob", jobRepository)
                .start(stepContributionConfigurationStep1())
                .next(stepContributionConfigurationStep2())
                .build();
    }

    @Bean
    public Step stepContributionConfigurationStep1() {
        return new StepBuilder("stepContributionConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepContributionConfigurationStep1");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepContributionConfigurationStep2() {
        return new StepBuilder("stepContributionConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepContributionConfigurationStep2");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
