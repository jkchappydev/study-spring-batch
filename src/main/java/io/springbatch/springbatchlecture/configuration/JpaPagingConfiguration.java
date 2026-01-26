package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer6;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JpaPagingConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingConfigurationJob() {
        return new JobBuilder("jpaPagingConfigurationJob", jobRepository)
                .start(jpaPagingConfigurationStep())
                .build();
    }

    @Bean
    public Step jpaPagingConfigurationStep() {
        return new StepBuilder("jpaPagingConfigurationStep", jobRepository)
                .<Customer6, Customer6>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer6> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Customer6>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                // .queryString("select c from Customer6 c") // 이렇게 하면 N+1 (Customer 목록을 가져온 뒤, 각 Customer 의 address 를 접근하기 때문에)
                .queryString("select c from Customer6 c join fetch c.address")
                .build();
    }

    @Bean
    public ItemWriter<Customer6> jpaPagingItemWriter() {
        return items -> {
            for (Customer6 customer : items) {
                System.out.println(customer.getAddress().getLocation());
            }
        };
    }

}
