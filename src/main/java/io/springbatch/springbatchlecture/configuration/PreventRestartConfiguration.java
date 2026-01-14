package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PreventRestartConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job preventRestartConfigurationJob() {
        return new JobBuilder("preventRestartConfigurationJob", jobRepository)
                .start(preventRestartConfigurationStep1())
                .next(preventRestartConfigurationStep2())
                .preventRestart()
                .build();
    }

    @Bean
    public Step preventRestartConfigurationStep1() {
        return new StepBuilder("preventRestartConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("preventRestartConfigurationStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step preventRestartConfigurationStep2() {
        return new StepBuilder("preventRestartConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // 1. 고의로 예외를 발생하여 Job을 실패처리
                    // throw new RuntimeException("preventRestartConfigurationStep2 was failed");

                    // 2. 이후에 정상 Step 으로 재시작 (하지만 위의 preventRestart() 때문에 재시작 안됨)
                    System.out.println("preventRestartConfigurationStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
