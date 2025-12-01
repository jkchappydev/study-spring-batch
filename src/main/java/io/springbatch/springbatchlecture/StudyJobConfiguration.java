package io.springbatch.springbatchlecture;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StudyJobConfiguration {

    // private final JobBuilderFactory jobBuilderFactory; // 5.0 이전
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job StudyJob() { // Singleton : 같은 이름의 Bean 생성 불가
        return new JobBuilder("StudyJob", jobRepository)
                .start(StudyStep1())
                .next(StudyStep2())
                .build();
    }

    @Bean
    public Step StudyStep1() {
        return new StepBuilder("StudyStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("StudyStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step StudyStep2() {
        return new StepBuilder("StudyStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("StudyStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
