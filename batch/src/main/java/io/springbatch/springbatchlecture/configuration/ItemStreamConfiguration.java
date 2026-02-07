package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ItemStreamConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job itemStreamConfigurationJob() {
        return new JobBuilder("itemStreamConfigurationJob", jobRepository)
                .start(chunkItemStreamStep())
                .build();
    }

    @Bean
    public Step chunkItemStreamStep() {
        return new StepBuilder("chunkItemStreamStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(customItemStreamReader())
                .writer(customItemStreamWriter())
                .build();
    }

    @Bean
    public CustomItemStreamReader customItemStreamReader() {
        List<String> items = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            items.add(String.valueOf(i));
        }

        return new CustomItemStreamReader(items);
    }

    @Bean
    public CustomItemStreamWriter customItemStreamWriter() {
        return new CustomItemStreamWriter();
    }

}
