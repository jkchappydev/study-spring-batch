package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.exception.CustomRetryException;
import io.springbatch.springbatchlecture.listener.CustomRetryListener;
import io.springbatch.springbatchlecture.processor.CustomRetryListenerItemProcessor;
import io.springbatch.springbatchlecture.reader.LinkedListRetryListenerItemReader;
import io.springbatch.springbatchlecture.writer.CustomRetryListenerItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RetryListenerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job retryListenerConfigurationJob() {
        return new JobBuilder("retryListenerConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(retryListenerConfigurationStep1())
                .build();
    }

    @Bean
    public Step retryListenerConfigurationStep1() {
        return new StepBuilder("retryListenerConfigurationStep1", jobRepository)
                .<Integer, String>chunk(10 , transactionManager)
                .reader(retryListenerItemReader())
                .processor(new CustomRetryListenerItemProcessor())
                .writer(new CustomRetryListenerItemWriter())
                .faultTolerant()
                .retry(CustomRetryException.class)
                .retryLimit(2)
                .listener(new CustomRetryListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> retryListenerItemReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        return new LinkedListRetryListenerItemReader<>(list);
    }

}
