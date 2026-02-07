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
public class FlowStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flowStepConfigurationJob() {
        return new JobBuilder("flowStepConfigurationJob", jobRepository)
                .start(flowStep())
                .next(f_Step2())
                .build();
    }

    private Step flowStep() {
        return new StepBuilder("flowStep", jobRepository)
                .flow(f_Flow())
                .build();
    }

    private Flow f_Flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("f_Flow");
        flowBuilder.start(f_Step1())
                .end();

        return flowBuilder.build();
    }

    @Bean
    public Step f_Step1() {
        return new StepBuilder("f_Step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("f_Step1 was executed");
                    throw new RuntimeException("f_Step1 was failed");
                    // return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step f_Step2() {
        return new StepBuilder("f_Step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("f_Step2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
