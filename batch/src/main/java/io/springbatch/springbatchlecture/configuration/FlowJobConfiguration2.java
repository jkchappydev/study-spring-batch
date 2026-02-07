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
public class FlowJobConfiguration2 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flowJobConfigurationJob2() {
        return new JobBuilder("flowJobConfigurationJob2", jobRepository)
                .start(flowStep())
                .next(fStep3())
                .end()
                .build();
    }

    @Bean
    public Flow flowStep() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowStep");
        flowBuilder.start(fStep1())
                .next(fStep2())
                .end();

        return flowBuilder.build(); // SimpleFlow 생성
    }

    @Bean
    public Step fStep1() {
        return new StepBuilder("fStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("fStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step fStep2() {
        return new StepBuilder("fStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("fStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step fStep3() {
        return new StepBuilder("fStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("fStep3 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
