package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class TaskletStepConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job taskletStepConfigurationJob() {
        return new JobBuilder("taskletStepConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(taskStep())
                .next(chunkStep())
                .build();
    }

    // Task 기반 프로세싱 - 단일 작업 기반
    @Bean
    public Step taskStep() {
        return new StepBuilder("taskStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("taskStep was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // Chunk 기반 프로세싱 - 대량 작업 기반
    @Bean
    public Step chunkStep() {
        return new StepBuilder("chunkStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5"))) // ItemReader, ListItemReader
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return item.toUpperCase();
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> items) throws Exception {
                        items.forEach(System.out::println);
                    }
                })
                .build();
    }

}
