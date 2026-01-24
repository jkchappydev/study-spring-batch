package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JsonConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jsonConfigurationJob() {
        return new JobBuilder("jsonConfigurationJob", jobRepository)
                .start(jsonConfigurationStep())
                .build();
    }

    @Bean
    public Step jsonConfigurationStep() {
        return new StepBuilder("jsonConfigurationStep", jobRepository)
                .<Customer2, Customer2>chunk(3, transactionManager)
                .reader(customItemReader())
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer2> customItemReader() {
        return new JsonItemReaderBuilder<Customer2>()
                .name("jsonReader")
                .resource(new ClassPathResource("customer.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer2.class))
                .build();
    }

    @Bean
    public ItemWriter<Customer2> customerItemWriter() {
        return items -> {
            for (Customer2 customer : items) {
                System.out.println(customer.toString());
            }
        };
    }

}
