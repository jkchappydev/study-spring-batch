package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.listener.JobAndStepAnnotationJobExecutionListener;
import io.springbatch.springbatchlecture.listener.JobAndStepJobExecutionListener;
import io.springbatch.springbatchlecture.listener.JobAndStepStepExecutionListener;
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
public class JobAndStepListenerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobAndStepStepExecutionListener jobAndStepStepExecutionListener; // Bean 으로 설정했을 때는 ExecutionContext 의 데이터를 Step 끼리 공유가능하다

    @Bean
    public Job jobAndStepListenerConfigurationJob() {
        return new JobBuilder("jobAndStepListenerConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jobAndStepListenerConfigurationStep1())
                .next(jobAndStepListenerConfigurationStep2())
                // .listener(new JobAndStepJobExecutionListener())
                .listener(new JobAndStepAnnotationJobExecutionListener())
                .build();
    }

    @Bean
    public Step jobAndStepListenerConfigurationStep1() {
        return new StepBuilder("jobAndStepListenerConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(jobAndStepStepExecutionListener)
                .build();
    }

    @Bean
    public Step jobAndStepListenerConfigurationStep2() {
        return new StepBuilder("jobAndStepListenerConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(jobAndStepStepExecutionListener)
                .build();
    }

}
