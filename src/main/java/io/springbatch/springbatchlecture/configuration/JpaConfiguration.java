package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.test.Customer;
import io.springbatch.springbatchlecture.test.Customer2;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JpaConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaConfigurationJob() {
        return new JobBuilder("jpaConfigurationJob", jobRepository)
                .start(jpaConfigurationStep())
                .build();
    }

    @Bean
    public Step jpaConfigurationStep() {
        return new StepBuilder("jpaConfigurationStep", jobRepository)
                .<Customer, Customer2>chunk(CHUNK_SIZE, transactionManager)
                .reader(jpaItemReader())
                .processor(jpaItemProcessor())
                .writer(jpaItemWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Customer> jpaItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setRowMapper(new JpaRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "G%");
        reader.setParameterValues(parameters);

        return reader;
    }

    @Bean
    public ItemProcessor<Customer, Customer2> jpaItemProcessor() {
        return new JpaItemProcessor();
    }

    @Bean
    public ItemWriter<Customer2> jpaItemWriter() {
        return new JpaItemWriterBuilder<Customer2>()
                .usePersist(true)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
