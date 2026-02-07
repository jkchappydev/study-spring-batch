package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.listener.CustomChunkListener;
import io.springbatch.springbatchlecture.listener.CustomItemProcessListener;
import io.springbatch.springbatchlecture.listener.CustomItemReadListener;
import io.springbatch.springbatchlecture.listener.CustomItemWriteListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChunkListenerConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job chunkListenerConfigurationJob() {
        return new JobBuilder("chunkListenerConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkListenerConfigurationStep1())
                .build();
    }

    @Bean
    public Step chunkListenerConfigurationStep1() {
        return new StepBuilder("chunkListenerConfigurationStep1", jobRepository)
                .<Integer, String>chunk(10, transactionManager)
                .listener(new CustomChunkListener())
                .reader(chunkListenerItemReader())
                .listener(new CustomItemReadListener())
                .processor(item -> {
                    // throw new RuntimeException("failed");
                    return "item" + item;
                })
                .listener(new CustomItemProcessListener())
                .writer(items -> {
                    System.out.println("items: " + items);
                })
                .listener(new CustomItemWriteListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> chunkListenerItemReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return new ListItemReader<>(list);
    }
}
