package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class LimitAllowConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job limitAllowJob() {
        return new JobBuilder("limitAllowJob", jobRepository)
                .start(limitStep())
                .next(allowStep())
                .build();
    }

    @Bean
    public Step limitStep() {
        return new StepBuilder("limitStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("limitStep was executed");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step allowStep() {
        return new StepBuilder("allowStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("allowStep was executed");
                    throw new RuntimeException("allowStep was failed"); // 고의로 실패
                    // return RepeatStatus.FINISHED;
                }, transactionManager)
                .startLimit(3)
                .build();
    }

}
