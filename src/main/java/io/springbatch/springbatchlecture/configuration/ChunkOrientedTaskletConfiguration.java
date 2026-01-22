package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class ChunkOrientedTaskletConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job chunkOrientedTaskletConfigurationJob() {
        return new JobBuilder("chunkOrientedTaskletConfigurationJob", jobRepository)
                .start(chunkOrientedTaskletStep1())
                .build();
    }

    @Bean
    public Step chunkOrientedTaskletStep1() {
        return new StepBuilder("chunkOrientedTaskletStep1", jobRepository)
                .<String, String>chunk(2, transactionManager)
                .reader(new ListItemReader<>(
                        Arrays.asList("item1", "item2", "item3", "item4", "item5", "item6")
                ))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return "my_" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        chunk.forEach(System.out::println);
                    }
                })
                .build();
    }

}
