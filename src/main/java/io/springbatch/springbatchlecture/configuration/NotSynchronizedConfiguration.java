package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.rowmapper.NotSynchronizedConfigurationRowMapper;
import io.springbatch.springbatchlecture.test.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class NotSynchronizedConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 60;

    @Bean
    public Job notSynchronizedConfigurationJob() {
        return new JobBuilder("notSynchronizedConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(notSynchronizedConfigurationStep())
                .build();
    }

    @Bean
    public Step notSynchronizedConfigurationStep() {
        return new StepBuilder("notSynchronizedConfigurationStep", jobRepository)
                .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(notSynchronizedCursorItemReader())
                .listener(new ItemReadListener<Customer>() {
                    @Override
                    public void beforeRead() {
                    }

                    @Override
                    public void afterRead(Customer item) {
                        System.out.println("Thread : " + Thread.currentThread().getName() + ", item.getId() = " + item.getId()); // 이 숫자가 중복으로 읽히면 안됨.
                    }

                    @Override
                    public void onReadError(Exception ex) {
                    }
                })
                .writer(notSynchronizedBatchItemWriter())
                .taskExecutor(notSynchronizedTaskExecutor())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Customer> notSynchronizedCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(this.dataSource)
                .rowMapper(new NotSynchronizedConfigurationRowMapper())
                .sql("select id, firstname, lastname, birthdate from customer")
                .name("NotSafetyReader")
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> notSynchronizedBatchItemWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();

        writer.setDataSource(this.dataSource);
        writer.setSql("insert into customer2 values (:id, :firstname, :lastname, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return writer;
    }

    @Bean
    public TaskExecutor notSynchronizedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("not-safety-thread-");

        return executor;
    }

}
