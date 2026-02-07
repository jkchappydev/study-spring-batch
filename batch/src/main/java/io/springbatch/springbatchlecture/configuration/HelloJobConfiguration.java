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
public class HelloJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager; // build.gradle 에서 Jpa 관련 의존성 추가해줘야 자동으로 JpaTransactionManager 가 들어옴

    @Bean
    public Job helloJob() {
        return new JobBuilder("helloJob", jobRepository)
                .start(helloStep1())
                .next(helloStep2())
                .build();
    }

    @Bean
    public Step helloStep1() {
        return new StepBuilder("helloStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // TODO: 비즈니스 로직 작성
                    System.out.println("======================");
                    System.out.println(" >> Hello Spring Batch");
                    System.out.println("======================");

                    // Tasklet 는 기본이 무한 반복이므로, 특정한 상황을 제외하고는 무한 반복되면 안된다.
                    // RepeatStatus.FINISHED 는 해당 Tasklet 를 한번만 실행하고 종료시키는 상태값이다.
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step helloStep2() {
        return new StepBuilder("helloStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("======================");
                    System.out.println(" >> Step2 was executed");
                    System.out.println("======================");

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
