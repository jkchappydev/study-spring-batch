package io.springbatch.springbatchlecture.practice.batch.job.api;

import io.springbatch.springbatchlecture.practice.batch.chunk.processor.ApiItemProcessor1;
import io.springbatch.springbatchlecture.practice.batch.chunk.processor.ApiItemProcessor2;
import io.springbatch.springbatchlecture.practice.batch.chunk.processor.ApiItemProcessor3;
import io.springbatch.springbatchlecture.practice.batch.chunk.writer.ApiItemWriter1;
import io.springbatch.springbatchlecture.practice.batch.chunk.writer.ApiItemWriter2;
import io.springbatch.springbatchlecture.practice.batch.chunk.writer.ApiItemWriter3;
import io.springbatch.springbatchlecture.practice.batch.classifier.ProcessorClassifier;
import io.springbatch.springbatchlecture.practice.batch.classifier.WriterClassifier;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiRequestVO;
import io.springbatch.springbatchlecture.practice.batch.domain.ProductVO;
import io.springbatch.springbatchlecture.practice.batch.partition.ProductPartitioner;
import io.springbatch.springbatchlecture.practice.service.ApiService1;
import io.springbatch.springbatchlecture.practice.service.ApiService2;
import io.springbatch.springbatchlecture.practice.service.ApiService3;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

// apiMasterStep
@Configuration
@RequiredArgsConstructor
public class ApiStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final ApiService1 apiService1;
    private final ApiService2 apiService2;
    private final ApiService3 apiService3;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Step apiMasterStep() throws Exception {
        return new StepBuilder("apiMasterStep", jobRepository)
                .partitioner(apiSlaveStep().getName(), productPartitioner()) // 첫번째 인자: SlaveStep 의 이름, 두번째 인자: Partition
                .step(apiSlaveStep()) // 어떤 SlaveStep 을 가지고 각각의 SlaveStep 으로 복제할 것인지 설정
                .gridSize(3)
                .taskExecutor(taskExecutor()) // 멀티 스레드 환경에서 작동하기 위해서 설정 필수
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3); // 기본 스레드 생성 갯수 최대 3개
        taskExecutor.setMaxPoolSize(6); // 최대 6개
        taskExecutor.setThreadNamePrefix("api-thread-");

        return taskExecutor;
    }

    @Bean
    public Step apiSlaveStep() throws Exception {
        return new StepBuilder("apiSlaveStep", jobRepository)
                .<ProductVO, ApiRequestVO>chunk(CHUNK_SIZE, transactionManager)
                .reader(productItemReader(null))
                .processor(productItemProcessor())
                .writer(productItemWriter())
                .build();
    }

    @Bean
    public ProductPartitioner productPartitioner() {
        ProductPartitioner partitioner = new ProductPartitioner();
        partitioner.setDataSource(dataSource);

        return partitioner;
    }

    // 실제 DB 에 접속해서 각각의 Product 타입 별로 값을 가져온 다음에 멀티 스레드 환경에서 실행될 수 있도록 구성
    @Bean
    @StepScope
    public ItemReader<ProductVO> productItemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {
        JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setPageSize(CHUNK_SIZE);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setRowMapper(new BeanPropertyRowMapper<>(ProductVO.class));

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("where type = :type");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);
        queryProvider.setSortKeys(sortKeys);

        // 멀티 스레드로 각각의 ItemReader 를 할당해야함. Partition 에는 멀티 스레드로 StepExecution, StepExecutionContext 가 만들어져서 각 스레드에게 할당된다.
        // 따라서 스레드들은 자기만의 StepExecutionContext 를 가지고 있다.
        // 그리고 각 스레드는 각각의 제품의 타입을 하나씩 가지고 있고,
        reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();

        return reader;
    }

    @Bean
    public ItemProcessor<? super ProductVO, ApiRequestVO> productItemProcessor() {
        ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO> processor = new ClassifierCompositeItemProcessor<>();

        // ProcessorClassifier<C, T>
        // C : Classifier 역할을 할 class 타입, T : 반환 값
        // C 에 들어오는 class 객체의 조건에 의해서 최종적으로 선택된 T 를 반환한다.
        ProcessorClassifier<ProductVO, ItemProcessor<?, ? extends ApiRequestVO>> classifier = new ProcessorClassifier<>();
        Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();
        processorMap.put("1", new ApiItemProcessor1());
        processorMap.put("2", new ApiItemProcessor2());
        processorMap.put("3", new ApiItemProcessor3());

        classifier.setProcessorMap(processorMap);
        processor.setClassifier(classifier);

        return processor;
    }

    @Bean
    public ItemWriter<? super ApiRequestVO> productItemWriter() {
        ClassifierCompositeItemWriter<ApiRequestVO> writer = new ClassifierCompositeItemWriter<>();

        WriterClassifier<ApiRequestVO, ItemWriter<? super ApiRequestVO>> classifier = new WriterClassifier<>();
        Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();
        writerMap.put("1", new ApiItemWriter1(apiService1));
        writerMap.put("2", new ApiItemWriter2(apiService2));
        writerMap.put("3", new ApiItemWriter3(apiService3));

        classifier.setWriterMap(writerMap);
        writer.setClassifier(classifier);

        return writer;
    }

}
