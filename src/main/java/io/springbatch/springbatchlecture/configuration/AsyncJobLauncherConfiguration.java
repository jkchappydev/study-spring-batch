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
public class AsyncJobLauncherConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job asyncJobLauncherJob() {
        return new JobBuilder("asyncJobLauncherJob", jobRepository)
                .start(jobLauncherStep3())
                .next(jobLauncherStep4())
                .build();
    }

    @Bean
    public Step jobLauncherStep3() {
        return new StepBuilder("jobLauncherStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(3000); // 비동기적 방식에서는 3초와 관계없이 우선적으로 JobExecution 이 반환된다.
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step jobLauncherStep4() {
        return new StepBuilder("jobLauncherStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
