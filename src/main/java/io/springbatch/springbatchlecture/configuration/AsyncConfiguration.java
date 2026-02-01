package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.listener.StopWatchJobListener;
import io.springbatch.springbatchlecture.rowmapper.AsyncConfigurationRowMapper;
import io.springbatch.springbatchlecture.test.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Configuration
@RequiredArgsConstructor
public class AsyncConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job asyncConfigurationJob() throws InterruptedException {
        return new JobBuilder("asyncConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                // .start(syncConfigurationStep()) // 동기식
                .start(asyncConfigurationStep()) // 비동기식
                .listener(new StopWatchJobListener())
                .build();
    }

    // 동기 Step
    @Bean
    public Step syncConfigurationStep() throws InterruptedException {
        return new StepBuilder("syncConfigurationStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(pagingItemReader())
                .processor(syncCustomItemProcessor())
                .writer(syncCustomItemWriter())
                .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer> syncCustomItemProcessor() throws InterruptedException {
        return new ItemProcessor<Customer, Customer>() {
            @Override
            public Customer process(Customer item) throws Exception {
                Thread.sleep(30);

                return new Customer(item.getId(), item.getFirstname().toUpperCase(), item.getLastname().toUpperCase(), item.getBirthdate());
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter<Customer> syncCustomItemWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(this.dataSource);
        writer.setSql("insert into customer2 values (:id, :firstname, :lastname, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return writer;
    }

    // 비동기 Step
    @Bean
    public Step asyncConfigurationStep() throws InterruptedException {
        return new StepBuilder("asyncConfigurationStep", jobRepository)
                .<Customer, Future<Customer>>chunk(100, transactionManager)
                .reader(pagingItemReader())
                .processor(asyncCustomItemProcessor())
                .writer(asyncCustomItemWriter())
                .build();
    }

    @Bean
    public AsyncItemProcessor<Customer, Customer> asyncCustomItemProcessor() throws InterruptedException {
        // spring-batch-integration 의존성 추가해야 한다.
        AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor<>();
        // 1. 일반 ItemProcessor 에게 처리 위임
        asyncItemProcessor.setDelegate(syncCustomItemProcessor());
        // 2. TaskExecutor 설정
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        // asyncItemProcessor.afterPropertiesSet(); // 이건 Bean 으로 설정하면 사용할 필요 없다.

        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<Customer> asyncCustomItemWriter() {
        // spring-batch-integration 의존성 추가해야 한다.
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();
        // 1. 일반 ItemWriter 에게 처리 위임
        asyncItemWriter.setDelegate(syncCustomItemWriter());

        return asyncItemWriter;
    }

    // 공통
    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader() {
        JdbcPagingItemReader<Customer> pagingItemReader = new JdbcPagingItemReader<>();

        pagingItemReader.setDataSource(this.dataSource);
        pagingItemReader.setFetchSize(300);
        pagingItemReader.setRowMapper(new AsyncConfigurationRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        pagingItemReader.setQueryProvider(queryProvider);

        return pagingItemReader;
    }

}
