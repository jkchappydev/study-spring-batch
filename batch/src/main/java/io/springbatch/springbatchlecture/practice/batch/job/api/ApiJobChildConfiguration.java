package io.springbatch.springbatchlecture.practice.batch.job.api;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// JobStep 용 Job
@Configuration
@RequiredArgsConstructor
public class ApiJobChildConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    // DB 로 부터 제품 정보를 읽고 나서 각 API 로 전달
    // 이 때, 제품 유형별로 독립된 스레드를 생성해서 실행하도록 멀티 스레드 환경 구축
    // 해당 환경 구현을 위해서 Partitioning 기능 활용
    private final Step apiMasterStep;
    private final JobLauncher jobLauncher; // JobStep 은 JobLauncher 클래스를 DI 받아야 한다.

    @Bean
    public Step jobSteps(Job childJob) {
        return new StepBuilder("jobSteps", jobRepository)
                .job(childJobs())
                .launcher(jobLauncher)
                .build();
    }

    // apiMasterStep 에게 Chunk 기반의 프로세싱을 멀티 스레드 환경에서 할 수 있도록 정의한 Job.
    @Bean
    public Job childJobs() {
        return new JobBuilder("childJobs", jobRepository)
                .start(apiMasterStep) // MasterStep 이 각각의 SlaveStep 을 생성한다.
                .build();
    }

}
