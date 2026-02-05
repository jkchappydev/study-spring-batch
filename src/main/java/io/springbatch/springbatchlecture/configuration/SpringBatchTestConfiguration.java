package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.rowmapper.SpringBatchStepConfigurationRowMapper;
import io.springbatch.springbatchlecture.test.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SpringBatchTestConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job springBatchTestConfigurationJob() {
        return new JobBuilder("springBatchTestConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(springBatchTestConfigurationStep1())
                .build();
    }

    @Bean
    public Step springBatchTestConfigurationStep1() {
        return new StepBuilder("springBatchTestConfigurationStep1", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(springBatchTestItemReader())
                .writer(springBatchTestItemWriter())
                .build();
    }
    @Bean
    public JdbcPagingItemReader<Customer> springBatchTestItemReader() {
        JdbcPagingItemReader<Customer> pagingItemReader = new JdbcPagingItemReader<>();

        pagingItemReader.setDataSource(this.dataSource);
        pagingItemReader.setPageSize(300);
        pagingItemReader.setFetchSize(300);
        pagingItemReader.setRowMapper(new SpringBatchStepConfigurationRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        pagingItemReader.setQueryProvider(queryProvider);

        return pagingItemReader;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> springBatchTestItemWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();

        writer.setDataSource(this.dataSource);
        writer.setSql("insert into customer2 values (:id, :firstname, :lastname, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();

        return writer;
    }

}
