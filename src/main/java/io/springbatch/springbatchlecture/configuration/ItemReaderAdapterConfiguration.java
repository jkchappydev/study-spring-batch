package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.CustomService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ItemReaderAdapterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job itemReaderAdapterConfigurationJob() {
        return new JobBuilder("itemReaderAdapterConfigurationJob", jobRepository)
                .start(itemReaderAdapterConfigurationStep())
                .build();
    }

    @Bean
    public Step itemReaderAdapterConfigurationStep() {
        return new StepBuilder("itemReaderAdapterConfigurationStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(itemReaderAdapterReader())
                .writer(itemReaderAdapterWriter())
                .build();
    }

    @Bean
    public ItemReader<String> itemReaderAdapterReader() {
        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(customService()); // CustomService 의
        reader.setTargetMethod("customRead"); // customRead() 호출

        return reader;
    }

    @Bean
    public Object customService() {
        return new CustomService();
    }

    @Bean
    public ItemWriter<String> itemReaderAdapterWriter() {
        return System.out::println;
    }

}
