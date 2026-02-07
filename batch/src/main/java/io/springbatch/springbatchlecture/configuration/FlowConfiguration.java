package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FlowConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // ========== FlowJob ==========
    @Bean
    public Job flowConfigurationJob() {
        return new JobBuilder("flowConfigurationJob", jobRepository)
                .start(jobConfigurationFlow())
                .next(flowConfigurationStep5())
                .end()
                .build();
    }

    @Bean
    public Flow jobConfigurationFlow() {
        return new FlowBuilder<Flow>("jobConfigurationFlow")
                .start(flowConfigurationStep3())
                .next(flowConfigurationStep4())
                .build();
    }

    @Bean
    public Step flowConfigurationStep3() {
        return new StepBuilder("flowConfigurationStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flowConfigurationStep3 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step flowConfigurationStep4() {
        return new StepBuilder("flowConfigurationStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flowConfigurationStep4 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step flowConfigurationStep5() {
        return new StepBuilder("flowConfigurationStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flowConfigurationStep5 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
