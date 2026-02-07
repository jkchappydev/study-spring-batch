package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

// Spring Batch 5.x 부터는 BasicBatchConfigurer 없어짐
// -> DefaultBatchConfiguration 상속해서 필요한 것만 override 하게 변경
// @Configuration
@RequiredArgsConstructor
public class CustomBatchConfigurer extends DefaultBatchConfiguration {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    @Override
    public JobRepository jobRepository() {
        try {
            JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
            factory.setDataSource(dataSource);
            factory.setTransactionManager(transactionManager);
            factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
            factory.setTablePrefix("SYSTEM_");

            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (Exception e) {
            throw new IllegalStateException("JobRepository 생성에 실패했습니다.", e);
        }
    }

}
