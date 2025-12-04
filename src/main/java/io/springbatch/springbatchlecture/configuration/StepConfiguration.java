package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.CustomTasklet;
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
public class StepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepConfigurationJob() {
        return new JobBuilder("stepConfigurationJob", jobRepository)
                .start(stepConfigurationStep1())
                .next(stepConfigurationStep2())
                .build();
    }

    @Bean
    public Step stepConfigurationStep1() {
        return new StepBuilder("stepConfigurationStep1", jobRepository)
                .tasklet(new CustomTasklet(), transactionManager) // 커스텀하게 생성한 Tasklet
                .build();
    }

    @Bean
    public Step stepConfigurationStep2() {
        return new StepBuilder("stepConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
