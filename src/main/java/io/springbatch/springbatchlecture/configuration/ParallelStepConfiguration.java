package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.listener.StopWatchJobListener;
import io.springbatch.springbatchlecture.tasklet.ParallelStepTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ParallelStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 스레드 갯수 생성 기준 -> 생성한 Flow 의 수 (parallelStepFlow1, parallelStepFlow2)
    // TaskExecutor 가 parallelStepFlow1 을 실행하는 스레드, parallelStepFlow2 를 실행하는 스레드를 만들어서 실행한다.
    @Bean
    public Job parallelStepConfigurationJob() {
        return new JobBuilder("parallelStepConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(parallelStepFlow1())
                .split(parallelStepConfigurationTaskExecutor()).add(parallelStepFlow2()) // 주석 처리하면 단일 스레드, 주석 해제하면 ParallelStep
                .end() // end() 필수
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    @StepScope // Step 실행마다 Tasklet 인스턴스를 새로 만들어 병렬 실행시 동시성 문제 방지
    public Tasklet parallelStepTasklet() {
        return new ParallelStepTasklet();
    }

    @Bean
    public Flow parallelStepFlow1() {
        TaskletStep taskletStep1 = new StepBuilder("parallelStep1", jobRepository)
                .tasklet(parallelStepTasklet(), transactionManager)
                .build();

        return new FlowBuilder<Flow>("parallelStepFlow1")
                .start(taskletStep1)
                .build();
    }

    @Bean
    public Flow parallelStepFlow2() {
        TaskletStep taskletStep2 = new StepBuilder("parallelStep2", jobRepository)
                .tasklet(parallelStepTasklet(), transactionManager)
                .build();

        TaskletStep taskletStep3 = new StepBuilder("parallelStep3", jobRepository)
                .tasklet(parallelStepTasklet(), transactionManager)
                .build();


        return new FlowBuilder<Flow>("parallelStepFlow2")
                .start(taskletStep2)
                .next(taskletStep3)
                .build();
    }

    @Bean
    public TaskExecutor parallelStepConfigurationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("async-thread-");

        return executor;
    }

}
