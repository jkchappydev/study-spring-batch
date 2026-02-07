package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.rowmapper.SynchronizedConfigurationRowMapper;
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
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SynchronizedConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private static final int CHUNK_SIZE = 60;

    @Bean
    public Job synchronizedConfigurationJob() {
        return new JobBuilder("synchronizedConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(synchronizedConfigurationStep())
                .build();
    }

    @Bean
    public Step synchronizedConfigurationStep() {
        return new StepBuilder("synchronizedConfigurationStep", jobRepository)
                .<Customer, Customer>chunk(CHUNK_SIZE, transactionManager)
                .reader(synchronizedCursorItemReader())
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
                .writer(synchronizedBatchItemWriter())
                .taskExecutor(synchronizedTaskExecutor())
                .build();
    }

    // 해당 부분을 Thread-safe 하게 변경한다.
    @Bean
    public SynchronizedItemStreamReader<Customer> synchronizedCursorItemReader() {
        JdbcCursorItemReader<Customer> notSafetyReader = new JdbcCursorItemReaderBuilder<Customer>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(this.dataSource)
                .rowMapper(new SynchronizedConfigurationRowMapper())
                .sql("select id, firstname, lastname, birthdate from customer")
                .name("SafetyReader")
                .build();

        return new SynchronizedItemStreamReaderBuilder<Customer>()
                .delegate(notSafetyReader)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Customer> synchronizedBatchItemWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();

        writer.setDataSource(this.dataSource);
        writer.setSql("insert into customer2 values (:id, :firstname, :lastname, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return writer;
    }

    @Bean
    public TaskExecutor synchronizedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("safety-thread-");

        return executor;
    }

}
