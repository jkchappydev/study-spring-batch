package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobScopeStepScopeConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job JobScopeStepScopeJob() {
        return new JobBuilder("JobScopeStepScopeJob", jobRepository)
                .start(jobScopeStepScopeStep1(null))
                .next(jobScopeStepScopeStep2())
                .listener(new CustomJobListener())
                .build();
    }

    @Bean
    @JobScope
    public Step jobScopeStepScopeStep1(@Value("#{jobParameters['message']}") String message) {
        System.out.println("message = " + message);
        return new StepBuilder("jobScopeStepScopeStep1", jobRepository)
                .tasklet(tasklet1(null), transactionManager)
                .build();
    }

    @Bean
    public Step jobScopeStepScopeStep2() {
        return new StepBuilder("jobScopeStepScopeStep2", jobRepository)
                .tasklet(tasklet2(null), transactionManager)
                .listener(new CustomStepListener())
                .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet1(@Value("#{jobExecutionContext['name']}") String name) {
        System.out.println("name = " + name);
        return (contribution, chunkContext) -> {
            System.out.println("tasklet1 was executed");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet tasklet2(@Value("#{stepExecutionContext['name2']}") String name2) {
        System.out.println("name2 = " + name2);
        return (contribution, chunkContext) -> {
            System.out.println("tasklet2 was executed");
            return RepeatStatus.FINISHED;
        };
    }

}
