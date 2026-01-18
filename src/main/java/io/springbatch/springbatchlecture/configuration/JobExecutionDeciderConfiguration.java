package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.CustomDecider;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobExecutionDeciderConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jobExecutionDeciderJob() {
        return new JobBuilder("jobExecutionDeciderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jobExecutionDeciderStep())
                .next(decider()) // start() 이 성공적으로 완료하면 decider() 를 실행한다.
                .from(decider()).on("ODD").to(oddStep()) // decider() 의 실행 후 상태가 ODD 이면 oddStep() 을 실행한다.
                .from(decider()).on("EVEN").to(evenStep()) // decider() 의 실행 후 상태가 EVEN 이면 evenStep() 을 실행한다.
                .end()
                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new CustomDecider();
    }

    @Bean
    public Step jobExecutionDeciderStep() {
        return new StepBuilder("jobExecutionDeciderStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("jobExecutionDeciderStep1 was executed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step evenStep() {
        return new StepBuilder("evenStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("evenStep was executed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

    @Bean
    public Step oddStep() {
        return new StepBuilder("oddStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("oddStep was executed");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }

}
