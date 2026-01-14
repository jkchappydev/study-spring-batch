package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
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
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // ========== SimpleJob ==========
    @Bean
    public Job simpleJobConfigurationJob() {
        return new JobBuilder("simpleJobConfigurationJob", jobRepository)
                .start(simpleJobConfigurationStep1())
                .next(simpleJobConfigurationStep2())
                .next(simpleJobConfigurationStep3())
                .build();
    }

    @Bean
    public Step simpleJobConfigurationStep1() {
        return new StepBuilder("simpleJobConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleJobConfigurationStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleJobConfigurationStep2() {
        return new StepBuilder("simpleJobConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("simpleJobConfigurationStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleJobConfigurationStep3() {
        return new StepBuilder("simpleJobConfigurationStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // 강제로 실패
                    chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED); // 상태코드를 FALIED 로 함
                    contribution.setExitStatus(ExitStatus.STOPPED); // 종료코드를 STOPPED 로 함
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
