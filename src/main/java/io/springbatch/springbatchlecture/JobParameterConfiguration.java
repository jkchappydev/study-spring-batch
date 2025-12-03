package io.springbatch.springbatchlecture;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobParameterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job parameterJob() {
        return new JobBuilder("parameterJob", jobRepository)
                .start(parameterStep1())
                .next(parameterStep2())
                .build();
    }

    @Bean
    public Step parameterStep1() {
        return new StepBuilder("parameterStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("parameterStep1 was executed");

                    // 1. contribution 을 이용한 jobParameters 가져오기
                    JobParameters jobParameters = contribution.getStepExecution().getJobExecution().getJobParameters();
                    jobParameters.getString("name");
                    jobParameters.getLong("seq");
                    jobParameters.getDate("date");
                    jobParameters.getDouble("age");

                    // 2. chunkContext 을 이용한 jobParameters 가져오기
                    Map<String, Object> jobParameters1 = chunkContext.getStepContext().getJobParameters();

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step parameterStep2() {
        return new StepBuilder("parameterStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("parameterStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
