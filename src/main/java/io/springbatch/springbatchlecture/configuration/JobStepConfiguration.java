package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 기본 Job
    @Bean
    public Job parentJob() {
        return new JobBuilder("parentJob", jobRepository)
                .start(jobStep(null))
                .next(jobStepStep2())
                .build();
    }

    @Bean
    public Step jobStep(JobLauncher jobLauncher) {
        return new StepBuilder("jobStep", jobRepository)
                .job(childJob()) // 외부 Job 실행
                .launcher(jobLauncher)
                .parametersExtractor(jobParametersExtractor())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putString("name", "user1");
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return null;
                    }
                })
                .build();
    }

    // StepExecution 안에는 ExecutionContext(= key/value 저장소)가 있다.
    // DefaultJobParametersExtractor 는 (JobStep 에서) 자식 Job 을 실행할 때
    // StepExecution 의 ExecutionContext 에 있는 지정한 key 값들과
    // 부모 Job 의 JobParameters 를 함께 읽어서, 자식 Job 에 전달할 JobParameters 를 만든다.
    // 여기서는 ExecutionContext 에서 "name" 키를 찾아 그 값을 JobParameter "name"으로 전달한다.
    private DefaultJobParametersExtractor jobParametersExtractor() {
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(new String[]{"name"});
        return extractor;
    }

    // 외부 Job
    @Bean
    public Job childJob() {
        return new JobBuilder("childJob", jobRepository)
                .start(jobStepStep1())
                .build();
    }

    @Bean
    public Step jobStepStep1() {
        return new StepBuilder("jobStepStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // throw new RuntimeException("jobStepStep1 was failed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step jobStepStep2() {
        return new StepBuilder("jobStepStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    throw new RuntimeException("jobStepStep2 was failed");
                    // return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
