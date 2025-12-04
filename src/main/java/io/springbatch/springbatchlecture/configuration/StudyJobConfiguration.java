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
public class StudyJobConfiguration {

    // private final JobBuilderFactory jobBuilderFactory; // 5.0 이전
    private final JobRepository jobRepository;

    // Step 작업을 하나의 트랜잭션으로 묶어 실패 시 전체 롤백시키기 위해 사용
    // (스프링 부트 자동 설정으로 JPA면 JpaTransactionManager, JDBC면 DataSourceTransactionManager 주입)
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
