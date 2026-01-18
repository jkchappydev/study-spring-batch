package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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
public class CustomExitStatusConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job customExitStatusJob() {
        return new JobBuilder("customExitStatusJob", jobRepository)
                .start(customExitStatusStep1())
                    .on("FAILED")
                    .to(customExitStatusStep2())
                    .on("PASS") // CustomExitStatus 를 구현한다.
                    .stop()
                    .end()
                .build();
    }

    @Bean
    public Step customExitStatusStep1() {
        return new StepBuilder("customExitStatusStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("customExitStatusStep1 was executed");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step customExitStatusStep2() {
        return new StepBuilder("customExitStatusStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("customExitStatusStep2 was executed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .listener(new PassCheckingListener()) // 사용자 정의 ExitStatus 는 afterStep() 에 구현해야 한다.
                .build();
    }

}
