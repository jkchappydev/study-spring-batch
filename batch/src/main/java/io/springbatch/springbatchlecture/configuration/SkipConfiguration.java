package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.exception.NoSkippableException;
import io.springbatch.springbatchlecture.exception.SkippableException;
import io.springbatch.springbatchlecture.processor.SkipItemProcessor;
import io.springbatch.springbatchlecture.writer.SkipItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SkipConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job skipConfigurationJob() {
        return new JobBuilder("skipConfigurationJob", jobRepository)
                .start(skipConfigurationStep())
                .build();
    }

    @Bean
    public Step skipConfigurationStep() {
        return new StepBuilder("skipConfigurationStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;

                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;

                        if (i == 3) {
                            System.out.println("\n==================== [SKIP @ READ] i=3 ====================\n");
                            throw new SkippableException("==== Skip ====");
                        }
                        System.out.println("ItemReader : " + i);
                        return i > 20 ? null : String.valueOf(i);
                    }
                })
                .processor(skipItemProcessor())
                .writer(skipItemWriter())
                .faultTolerant()
                // .skip(SkippableException.class)
                // .skipLimit(3)
                .noSkip(NoSkippableException.class)
                // 직접 SkipPolicy 인터페이스 구현
                .skipPolicy(limitCheckingItemSkipPolicy())
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> skipItemProcessor() {
        return new SkipItemProcessor();
    }

    @Bean
    public ItemWriter<? super String> skipItemWriter() {
        return new SkipItemWriter();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        // 1. 예외 대상 설정
        exceptionClass.put(SkippableException.class, true);
        // 2. limitCount 설정
        LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(3, exceptionClass);

        return limitCheckingItemSkipPolicy;
    }

}
