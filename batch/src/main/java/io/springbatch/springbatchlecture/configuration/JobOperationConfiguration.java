package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobOperationConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jobOperationConfigurationJob() {
        return new JobBuilder("jobOperationConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepOperationConfigurationStep1())
                .next(stepOperationConfigurationStep2())
                .build();
    }

    @Bean
    public Step stepOperationConfigurationStep1() {
        return new StepBuilder("stepOperationConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepOperationConfigurationStep1 was executed");
                    // 60초 동안 1초씩 자면서 stop 요청 오면 종료
                    for (int i = 0; i < 60; i++) {
                        if (contribution.getStepExecution().isTerminateOnly()) break;
                        Thread.sleep(1000);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepOperationConfigurationStep2() {
        return new StepBuilder("stepOperationConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepOperationConfigurationStep2 was executed");
                    // 60초 동안 1초씩 자면서 stop 요청 오면 종료
                    for (int i = 0; i < 60; i++) {
                        if (contribution.getStepExecution().isTerminateOnly()) break;
                        Thread.sleep(1000);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
