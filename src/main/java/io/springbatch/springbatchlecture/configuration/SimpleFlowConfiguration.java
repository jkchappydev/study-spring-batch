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
public class SimpleFlowConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // ==== Flow Job 구성 ====
    @Bean
    public Job simpleFlowConfigurationJob() {
        return new JobBuilder("simpleFlowConfigurationJob", jobRepository)
                // ==== SimpleFlow ====
                .start(simpleFlow())
                .next(simpleFlowStep3())
                .end() // SimpleFlow 생성
                // =====================
                .build();
    }

    @Bean
    public Flow simpleFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow");
        flowBuilder.start(simpleFlowStep1())
                .next(simpleFlowStep2())
                .end(); // SimpleFlow 생성

        return flowBuilder.build();
    }

    @Bean
    public Step simpleFlowStep1() {
        return new StepBuilder("simpleFlowStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleFlowStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep2() {
        return new StepBuilder("simpleFlowStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleFlowStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep3() {
        return new StepBuilder("simpleFlowStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleFlowStep3 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
