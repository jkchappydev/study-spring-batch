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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class ChunkConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job chunkConfigurationJob() {
        return new JobBuilder("chunkConfigurationJob", jobRepository)
                .start(chunkConfigurationStep())
                .next(normalStep())
                .build();
    }

    @Bean
    public Step chunkConfigurationStep() {
        return new StepBuilder("chunkConfigurationStep", jobRepository)
                .<String, String>chunk(5, transactionManager) // 첫번째 String : Input 타입 / 두번째 String : Output 타입
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception { // ItemProcessor 는 하나의 item 이 개별 처리되도록 인자값이 전달됨
                        Thread.sleep(300); // 테스트 용도 0.3초 지연
                        System.out.println("item = " + item);
                        return "my" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception { // ItemWriter 는 하나의 Item 이 아닌, 일괄 처리되도록 인자값으로 Chunk(ItemList) 가 전달된다.
                        Thread.sleep(300); // 테스트 용도 0.3초 지연
                        System.out.println("chunk = " + chunk);
                    }
                })
                .build();
    }

    @Bean
    public Step normalStep() {
        return new StepBuilder("normalStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("normalStep was executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
