package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer7;
import io.springbatch.springbatchlecture.rowmapper.CustomRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JsonConfiguration2 {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;
    private final DataSource dataSource;

    @Bean
    public Job jsonConfiguration2Job() {
        return new JobBuilder("jsonConfiguration2Job", jobRepository)
                .start(jsonConfiguration2Step())
                .build();
    }

    @Bean
    public Step jsonConfiguration2Step() {
        return new StepBuilder("jsonConfiguration2Step", jobRepository)
                .<Customer7, Customer7>chunk(CHUNK_SIZE, transactionManager)
                .reader(jsonConfiguration2Reader())
                .writer(jsonConfiguration2Writer())
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Customer7> jsonConfiguration2Reader() {
        JdbcPagingItemReader<Customer7> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setRowMapper(new CustomRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("select id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "D%");
        reader.setParameterValues(parameters);

        return reader;
    }

    @Bean
    public ItemWriter<Customer7> jsonConfiguration2Writer() {
        // OS 구분 없이 프로젝트 루트 기준으로 src/main/resources/customer2.json
        Path output = Paths.get(System.getProperty("user.dir"),
                "src", "main", "resources", "customer2.json");

        return new JsonFileItemWriterBuilder<Customer7>()
                .name("jsonFileWriter")
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource(output.toFile()))
                .build();
    }

}
