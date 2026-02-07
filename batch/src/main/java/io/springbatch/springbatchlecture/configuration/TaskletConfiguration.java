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
public class TaskletConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job taskletConfigurationJob() {
        return new JobBuilder("taskletConfigurationJob", jobRepository)
                .start(taskletStep1())
                .next(taskletStep2())
                .build();
    }

    // 익명 클래스 Tasklet 사용
    @Bean
    public Step taskletStep1() {
        return new StepBuilder("taskletStep1", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("taskletStep1 was executed");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    // 직접 구현한 Tasklet 사용
    @Bean
    public Step taskletStep2() {
        return new StepBuilder("taskletStep2", jobRepository)
                .tasklet(new CustomTasklet(), transactionManager)
                .build();
    }
    
}
