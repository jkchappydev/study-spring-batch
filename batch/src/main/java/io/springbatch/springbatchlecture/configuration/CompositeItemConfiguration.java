package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.processor.CustomItemProcessor;
import io.springbatch.springbatchlecture.processor.CustomItemProcessor2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CompositeItemConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job compositeItemConfigurationJob() {
        return new JobBuilder("compositeItemConfigurationJob", jobRepository)
                .start(compositeItemConfigurationStep())
                .build();
    }

    @Bean
    public Step compositeItemConfigurationStep() {
        return new StepBuilder("compositeItemConfigurationStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;

                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;

                        return i > 10 ? null : "item";
                    }
                })
                .processor(customItemProcessor())
                .writer(System.out::println)
                .build();
    }

    @Bean
    public ItemProcessor<String, String> customItemProcessor() {
        List itemProcessors = new ArrayList();
        itemProcessors.add(new CustomItemProcessor());
        itemProcessors.add(new CustomItemProcessor2());

        return new CompositeItemProcessorBuilder<>()
                .delegates(itemProcessors)
                .build();
    }

}
