package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer3;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcCursorConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorConfigurationJob() {
        return new JobBuilder("jdbcCursorConfigurationJob", jobRepository)
                .start(jdbcCursorConfigurationStep())
                .build();
    }

    @Bean
    public Step jdbcCursorConfigurationStep() {
        return new StepBuilder("jdbcCursorConfigurationStep", jobRepository)
                .<Customer3, Customer3>chunk(CHUNK_SIZE, transactionManager)
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer3> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer3>()
                .name("jdbcCursorItemReader")
                .fetchSize(CHUNK_SIZE)
                .sql("select id, firstName, lastName, birthdate from customer where firstName like ? order by lastName, firstName")
                .beanRowMapper(Customer3.class)
                .queryArguments("A%")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<Customer3> jdbcCursorItemWriter() {
        return items -> {
            for (Customer3 item : items) {
                System.out.println(item);
            }
        };
    }

}
