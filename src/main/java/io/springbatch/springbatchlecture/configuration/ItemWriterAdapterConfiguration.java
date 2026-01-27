package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.CustomService2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ItemWriterAdapterConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job itemWriterAdapterConfigurationJob() {
        return new JobBuilder("itemWriterAdapterConfigurationJob", jobRepository)
                .start(itemWriterAdapterConfigurationStep())
                .build();
    }

    @Bean
    public Step itemWriterAdapterConfigurationStep() {
        return new StepBuilder("itemWriterAdapterConfigurationStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;

                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;

                        return i > 10 ? null : "item" + i;
                    }
                })
                .writer(itemWriterAdapterWriter())
                .build();
    }

    @Bean
    public CustomService2 customService2() {
        return new CustomService2();
    }

    @Bean
    public ItemWriter<String> itemWriterAdapterWriter() {
        ItemWriterAdapter<String> writer = new ItemWriterAdapter<String>();
        writer.setTargetObject(customService2());
        writer.setTargetMethod("customWrite");

        return writer;
    }

}