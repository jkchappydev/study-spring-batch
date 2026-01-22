package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class ItemReaderItemProcessorItemWriterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job ItemReaderItemProcessorItemWriterConfigurationJob() {
        return new JobBuilder("ItemReaderItemProcessorItemWriterConfigurationJob", jobRepository)
                .start(ItemReaderItemProcessorItemWriterStep1())
                .build();
    }

    @Bean
    public Step ItemReaderItemProcessorItemWriterStep1() {
        return new StepBuilder("ItemReaderItemProcessorItemWriterStep", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new CustomItemReader(
                Arrays.asList(
                        new Customer("user1"),
                        new Customer("user2"),
                        new Customer("user3")
                )
        );
    }

    @Bean
    public ItemProcessor<? super Customer, ? extends Customer> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<? super Customer> itemWriter() {
        return new CustomItemWriter();
    }

}
