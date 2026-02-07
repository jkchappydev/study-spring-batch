package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ValidatorConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job validatorConfigurationJob() {
        return new JobBuilder("validatorConfigurationJob", jobRepository)
                .start(validatorConfigurationStep1())
                .next(validatorConfigurationStep2())
                // .validator(new CustomJobParametersValidator()) // 커스텀
                .validator(new DefaultJobParametersValidator( // 배열 형태의 인자를 두 개 받는데, 첫번째는 Key, 두번째는 옵션 Key
                        new String[]{"name", "date"}, new String[]{"count"})
                )
                .build();
    }

    @Bean
    public Step validatorConfigurationStep1() {
        return new StepBuilder("validatorConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("validatorConfigurationStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step validatorConfigurationStep2() {
        return new StepBuilder("validatorConfigurationStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("validatorConfigurationStep2 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
