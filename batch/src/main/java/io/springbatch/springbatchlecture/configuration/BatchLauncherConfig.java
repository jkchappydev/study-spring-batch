package io.springbatch.springbatchlecture.configuration;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BatchLauncherConfig {

    // 동기식 설정
    @Bean(name = "syncJobLauncher")
    public JobLauncher syncJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(new org.springframework.core.task.SyncTaskExecutor());
        launcher.afterPropertiesSet();
        return launcher;
    }

    // 비동기식 설정
    @Bean
    public TaskExecutor asyncBatchTaskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(8);
        ex.setQueueCapacity(100);
        ex.setThreadNamePrefix("batch-");
        ex.initialize();
        return ex;
    }

    @Bean(name = "asyncJobLauncher")
    public JobLauncher asyncJobLauncher(
            JobRepository jobRepository,
            @Qualifier("asyncBatchTaskExecutor") TaskExecutor taskExecutor
    ) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(taskExecutor);
        launcher.afterPropertiesSet();
        return launcher;
    }

}
