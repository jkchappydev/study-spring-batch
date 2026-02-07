package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StartNextConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job startNextConfigurationJob() {
        return new JobBuilder("startNextConfigurationJob", jobRepository)
                .start(startNextFlowA())
                .next(startNextStep3())
                .next(startNextFlowB())
                .next(startNextStep6())
                .end()
                .build();
    }

    @Bean
    public Flow startNextFlowA() {
        return new FlowBuilder<Flow>("startNextFlowA")
                .start(startNextStep1())
                .next(startNextStep2())
                .build();
    }

    @Bean
    public Flow startNextFlowB() {
        return new FlowBuilder<Flow>("startNextFlowB")
                .start(startNextStep4())
                .next(startNextStep5())
                .build();
    }

    @Bean
    public Step startNextStep1() {
        return new StepBuilder("startNextStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("startNextStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step startNextStep2() {
        return new StepBuilder("startNextStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("startNextStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step startNextStep3() {
        return new StepBuilder("startNextStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("startNextStep3 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step startNextStep4() {
        return new StepBuilder("startNextStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("startNextStep4 was executed");
                    throw new RuntimeException("startNextStep4 was failed");
                    //return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step startNextStep5() {
        return new StepBuilder("startNextStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("startNextStep5 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step startNextStep6() {
        return new StepBuilder("startNextStep6", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("startNextStep6 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
