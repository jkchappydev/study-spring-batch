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

    @Bean
    public Job simpleFlowConfigurationJob() {
        return new JobBuilder("simpleFlowConfigurationJob", jobRepository)
                .start(simpleFlow1())
                    .on("COMPLETED")
                    .to(simpleFlow2())
                .from(simpleFlow1())
                    .on("FAILED")
                    .to(simpleFlow3())
                .end().build();
    }

    @Bean
    public Flow simpleFlow1() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow");
        flowBuilder.start(simpleFlowStep1())
                .next(simpleFlowStep2())
                .end();

        return flowBuilder.build();
    }

    @Bean
    public Flow simpleFlow2() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow");
        flowBuilder.start(simpleFlow3())
                .next(simpleFlowStep5())
                .next(simpleFlowStep6())
                .end();

        return flowBuilder.build();
    }

    @Bean
    public Flow simpleFlow3() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlow");
        flowBuilder.start(simpleFlowStep3())
                .next(simpleFlowStep4())
                .end();

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
                    // throw new RuntimeException("simpleFlowStep2 was failed");
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

    @Bean
    public Step simpleFlowStep4() {
        return new StepBuilder("simpleFlowStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleFlowStep4 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep5() {
        return new StepBuilder("simpleFlowStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleFlowStep5 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleFlowStep6() {
        return new StepBuilder("simpleFlowStep6", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleFlowStep6 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
