package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.partitioner.ColumnRangePartitioner;
import io.springbatch.springbatchlecture.rowmapper.PartitioningConfigurationRowMapper;
import io.springbatch.springbatchlecture.test.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PartitioningConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job partitioningConfigurationJob() {
        return new JobBuilder("partitioningConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(masterStep())
                .build();
    }

    @Bean
    public Step masterStep() {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner(slaveStep().getName(), partitioner())
                .step(slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Step slaveStep() {
        return new StepBuilder("slaveStep", jobRepository)
                .<Customer, Customer>chunk(100, transactionManager)
                .reader(partitioningPagingItemReader(null, null))
                .writer(partitioningBatchItemWriter())
                .build();
    }

    @Bean
    public Partitioner partitioner() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
        partitioner.setColumn("id");
        partitioner.setDataSource(dataSource);
        partitioner.setTable("customer");

        return partitioner;
    }


    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> partitioningPagingItemReader(
            // 각 파티션(스레드)은 고유한 StepExecution을 가지며, StepExecution에는 ExecutionContext가 존재한다.
            // Partitioner가 파티션별 ExecutionContext에 minValue/maxValue를 저장하고,
            // StepScope 빈은 @Value("#{stepExecutionContext[...]")로 해당 값을 꺼내 Reader에 주입받는다.
            // 그 결과 파티션(스레드)마다 Reader의 조회 범위가 분리되어 동일 데이터 동시 읽기를 방지할 수 있다.
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) {
        System.out.println("reading : " + minValue + " to " + maxValue);

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new PartitioningConfigurationRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where id >= " + minValue + " and id <= " +  maxValue);

        Map<String, Order> sortKeys = new HashMap<>(1); // 정렬 키를 1개(id)만 넣을거라 불필요한 리사이징을 줄이려고 Map 초기 용량을 1로 설정.
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> partitioningBatchItemWriter() {
        JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();

        writer.setDataSource(this.dataSource);
        writer.setSql("insert into customer2 values (:id, :firstname, :lastname, :birthdate)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return writer;
    }

}
