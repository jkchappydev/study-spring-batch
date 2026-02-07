package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer8;
import io.springbatch.springbatchlecture.rowmapper.CustomRowMapper;
import io.springbatch.springbatchlecture.rowmapper.JdbcBatchRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JdbcBatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;
    private final DataSource dataSource;

    @Bean
    public Job jdbcBatchConfigurationJob() {
        return new JobBuilder("jdbcBatchConfigurationJob", jobRepository)
                .start(jdbcBatchConfigurationStep())
                .build();
    }

    @Bean
    public Step jdbcBatchConfigurationStep() {
        return new StepBuilder("jdbcBatchConfigurationStep", jobRepository)
                .<Customer8, Customer8>chunk(CHUNK_SIZE, transactionManager)
                .reader(jdbcBatchConfigurationReader())
                .writer(jdbcBatchConfigurationWriter())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Customer8> jdbcBatchConfigurationReader() {
        JdbcPagingItemReader<Customer8> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setRowMapper(new JdbcBatchRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "E%");
        reader.setParameterValues(parameters);

        return reader;
    }

    @Bean
    public ItemWriter<Customer8> jdbcBatchConfigurationWriter() {
        return new JdbcBatchItemWriterBuilder<Customer8>()
                .dataSource(dataSource)
                .sql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)")
                .beanMapped() // 여기서는 beanMapped() 설정을 해야 JdbcBatchRowMapper 와 매핑된다.
                .build();
    }

}