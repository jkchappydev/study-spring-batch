package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
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
public class JobRepositoryConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobExecutionListener jobRepositoryListener;

    @Bean
    public Job jobRepositoryJob() {
        return new JobBuilder("jobRepositoryJob", jobRepository)
                .start(jobRepositoryStep1())
                .next(jobRepositoryStep2())
                .listener(jobRepositoryListener) // 리스너 등록 (나중에 학습)
                .build();
    }

    @Bean
    public Step jobRepositoryStep1() {
        return new StepBuilder("jobRepositoryStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                   return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step jobRepositoryStep2() {
        return new StepBuilder("jobRepositoryStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
