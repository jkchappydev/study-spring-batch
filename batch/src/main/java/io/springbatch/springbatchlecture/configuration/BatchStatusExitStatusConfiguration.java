package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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
public class BatchStatusExitStatusConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // SimpleJob
    @Bean
    public Job batchStatusExitStatusJob() {
        return new JobBuilder("batchStatusExitStatusJob", jobRepository)
                .start(batchStatusExitStatusStep1())
                .next(batchStatusExitStatusStep2())
                .build();
    }

    // FlowJob
    @Bean
    public Job batchStatusExitStatusFlowJob() {
        return new JobBuilder("batchStatusExitStatusFlowJob", jobRepository)
                .start(batchStatusExitStatusStep1())
                    .on("FAILED")
                    .to(batchStatusExitStatusStep2())
                .end()
                .build();
    }

    @Bean
    public Step batchStatusExitStatusStep1() {
        return new StepBuilder("batchStatusExitStatusStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("batchStatusExitStatusStep1 was executed");
                    contribution.setExitStatus(ExitStatus.FAILED); // FlowJob - 임의로 Step 종료상태를 FAILED 로 설정
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step batchStatusExitStatusStep2() {
        return new StepBuilder("batchStatusExitStatusStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("batchStatusExitStatusStep2 was executed");
                    // contribution.setExitStatus(ExitStatus.FAILED); // SimpleJob - 임의로 Step 종료상태를 FAILED 로 설정
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
