package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.listener.MultiThreadItemProcessListener;
import io.springbatch.springbatchlecture.listener.MultiThreadItemReadListener;
import io.springbatch.springbatchlecture.listener.MultiThreadItemWriteListener;
import io.springbatch.springbatchlecture.listener.StopWatchJobListener;
import io.springbatch.springbatchlecture.rowmapper.MultiThreadStepConfigurationRowMapper;
import io.springbatch.springbatchlecture.test.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MultiThreadStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job multiThreadStepJob() {
        return new JobBuilder("multiThreadStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multiThreadStepStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step multiThreadStepStep() {
        return new StepBuilder("multiThreadStepStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(multiThreadPagingItemReader())
                .listener(new MultiThreadItemReadListener())
                .processor((ItemProcessor<Customer, Customer>) item -> item)
                .listener(new MultiThreadItemProcessListener())
                .writer(multiThreadStepCustomItemWriter())
                .listener(new MultiThreadItemWriteListener())
                .taskExecutor(multiThreadTaskExecutor()) // taskExecutor 를 설정하지 않으면 단일 스레드로 동작함 (설정하면 비동기적 멀티 스레드로 동작함)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> multiThreadStepCustomItemWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(this.dataSource);
        writer.setSql("insert into customer2 values (:id, :firstname, :lastname, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return writer;
    }

    @Bean
    public TaskExecutor multiThreadTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // 4개의 스레드를 생성
        taskExecutor.setMaxPoolSize(8); // 4개의 스레다가 작업을 처리하고 있는데 처리 안된 작업들이 있는경우 최대 몇개의 스레드를 더 생성할 것인지 설정
        taskExecutor.setThreadNamePrefix("async-thread"); // 스레드 이름

        return taskExecutor;
    }

    // 공통
    @Bean
    public JdbcPagingItemReader<Customer> multiThreadPagingItemReader() {
        JdbcPagingItemReader<Customer> pagingItemReader = new JdbcPagingItemReader<>();

        pagingItemReader.setDataSource(this.dataSource);
        pagingItemReader.setFetchSize(300);
        pagingItemReader.setRowMapper(new MultiThreadStepConfigurationRowMapper());

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
