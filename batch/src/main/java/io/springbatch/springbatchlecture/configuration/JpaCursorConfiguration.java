package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer4;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JpaCursorConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorConfigurationJob() {
        return new JobBuilder("jpaCursorConfigurationJob", jobRepository)
                .start(jpaCursorConfigurationStep())
                .build();
    }

    @Bean
    public Step jpaCursorConfigurationStep() {
        return new StepBuilder("jpaCursorConfigurationStep", jobRepository)
                .<Customer4, Customer4>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaCursorItemReader())
                .writer(jpaCursorItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer4> jpaCursorItemReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "B%");

        return new JpaCursorItemReaderBuilder<Customer4>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer4 c where c.firstname like :firstname")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemWriter<Customer4> jpaCursorItemWriter() {
        return items -> {
            for (Customer4 item : items) {
                System.out.println(item);
            }
        };
    }

}
