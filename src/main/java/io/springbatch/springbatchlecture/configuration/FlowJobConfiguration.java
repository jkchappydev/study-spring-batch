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
public class FlowJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flowJobConfigurationJob() {
        return new JobBuilder("flowJobConfigurationJob", jobRepository)
                // flowJobStep1 이 성공이면 flowJobStep3() 실행
                .start(flowJobStep1())
                .on("COMPLETED")
                .to(flowJobStep3())
                // 추가 정의: flowJobStep1 이 실패하면 flowJobStep2() 실행
                .from(flowJobStep1())
                .on("FAILED")
                .to(flowJobStep2())
                .end()
                .build();
    }

    @Bean
    public Step flowJobStep1() {
        return new StepBuilder("flowJobStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flowJobStep1 was executed");
                    // throw new RuntimeException("flowJobStep1 was failed"); // flowJobStep1 강제 실패
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // flowJobStep1 이 실패하면 여기로
    @Bean
    public Step flowJobStep2() {
        return new StepBuilder("flowJobStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flowJobStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // flowJobStep1 이 성공하면 여기로
    @Bean
    public Step flowJobStep3() {
        return new StepBuilder("flowJobStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("flowJobStep3 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
