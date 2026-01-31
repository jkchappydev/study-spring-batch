package io.springbatch.springbatchlecture.template;

import io.springbatch.springbatchlecture.exception.RetryableException;
import io.springbatch.springbatchlecture.test.Customer3;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration2 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job retryConfigurationJob2() {
        return new JobBuilder("retryConfigurationJob2", jobRepository)
                .start(retryConfigurationStep2())
                .build();
    }

    @Bean
    public Step retryConfigurationStep2() {
        return new StepBuilder("retryConfigurationStep2", jobRepository)
                .<String, Customer3>chunk(CHUNK_SIZE, transactionManager)
                .reader(retryItemReader2())
                .processor(retryItemProcessor2())
                .writer(items -> items.forEach(System.out::println))
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .build();
    }

    @Bean
    public ListItemReader<String> retryItemReader2() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemProcessor<? super String, Customer3> retryItemProcessor2() {
        return new RetryItemProcessor2();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        // 1. 예외 대상 설정
        exceptionClass.put(RetryableException.class, true);

        // 2. 지연 시간 설정
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000); // 재시도시 2초의 시간이 흐른 후 재시도함

        // 3. limitCount 설정
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        return retryTemplate;
    }

}