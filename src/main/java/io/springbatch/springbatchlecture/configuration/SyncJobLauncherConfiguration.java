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
public class SyncJobLauncherConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job syncJobLauncherJob() {
        return new JobBuilder("syncJobLauncherJob", jobRepository)
                .start(jobLauncherStep1())
                .next(jobLauncherStep2())
                .build();
    }

    @Bean
    public Step jobLauncherStep1() {
        return new StepBuilder("jobLauncherStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(3000); // 동기적 방식에서는 3초의 시간이 흐른 후에 JobExecution 이 반환된다.
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step jobLauncherStep2() {
        return new StepBuilder("jobLauncherStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
