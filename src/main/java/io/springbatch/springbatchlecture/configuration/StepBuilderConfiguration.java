package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class StepBuilderConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepBuilderConfigurationJob() {
        return new JobBuilder("stepBuilderConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderConfigurationStep1())
                .next(stepBuilderConfigurationStep2())
                .next(stepBuilderConfigurationStep3())
                .build();
    }

    // TaskletStepBuilder
    @Bean
    public Step stepBuilderConfigurationStep1() {
        return new StepBuilder("stepBuilderConfigurationStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("stepBuilderConfigurationStep1 was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    // SimpleStepBuilder
    @Bean
    public Step stepBuilderConfigurationStep2() {
        return new StepBuilder("stepBuilderConfigurationStep2", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(new ItemReader<String>() {
                    @Override
                    public String read() throws Exception {
                        return null;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> items) throws Exception {

                    }
                })
                .build();
    }

    // PartitionStepBuilder
    @Bean
    public Partitioner stepBuilderConfigurationPartitioner() {
        return gridSize -> Collections.emptyMap(); // 형태만
    }

    @Bean
    public Step stepBuilderConfigurationStep3() {
        return new StepBuilder("stepBuilderConfigurationStep3", jobRepository)
                .partitioner("stepBuilderConfigurationStep1", stepBuilderConfigurationPartitioner())
                .step(stepBuilderConfigurationStep1())
                .gridSize(2)
                .build();
    }

    // JobStepBuilder
    @Bean
    public Job stepBuilderConfigurationJob2() {
        return new JobBuilder("stepBuilderConfigurationJob2", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderConfigurationStep1())
                .next(stepBuilderConfigurationStep2())
                .next(stepBuilderConfigurationStep3())
                .build();
    }

    @Bean
    public Step stepBuilderConfigurationStep4() {
        return new StepBuilder("stepBuilderConfigurationStep4", jobRepository)
                .job(stepBuilderConfigurationJob2())
                .build();
    }

    // FlowStepBuilder
    @Bean
    public Job stepBuilderConfigurationJob3() {
        return new JobBuilder("stepBuilderConfigurationJob3", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderConfigurationFlow())
                .next(stepBuilderConfigurationStep3())
                .end()
                .build();
    }

    @Bean
    public Flow stepBuilderConfigurationFlow() {
        return new FlowBuilder<Flow>("stepBuilderConfigurationFlow")
                .start(stepBuilderConfigurationStep2())
                .build();
    }

    @Bean
    public Step stepBuilderConfigurationStep5() {
        return new StepBuilder("stepBuilderConfigurationStep5", jobRepository)
                .flow(stepBuilderConfigurationFlow())
                .build();
    }

}
