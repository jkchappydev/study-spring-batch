package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.processor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ClassifierCompositeItemProcessorConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job classifierCompositeItemProcessorJob() {
        return new JobBuilder("classifierCompositeItemProcessorJob", jobRepository)
                .start(classifierCompositeItemProcessorStep())
                .build();
    }

    @Bean
    public Step classifierCompositeItemProcessorStep() {
        return new StepBuilder("classifierCompositeItemProcessorStep", jobRepository)
                .<ProcessorInfo, ProcessorInfo>chunk(CHUNK_SIZE, transactionManager)
                .reader(new ItemReader<ProcessorInfo>() {
                    int i = 0;
                    @Override
                    public ProcessorInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        ProcessorInfo processorInfo = ProcessorInfo.builder().id(i).build();
                        return i > 3 ? null : processorInfo;
                    }
                })
                .processor(customClassifierItemProcessor()) // 1) ItemReader 가 만든 Item 들이 Chunk 로 묶여서 Processor 로 넘어간다.
                .writer(System.out::println)  // 4) Processor 결과를 ItemWriter 가 받아서 출력한다.
                .build();
    }

    @Bean
    public ItemProcessor<ProcessorInfo, ProcessorInfo> customClassifierItemProcessor() {
        ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> processor = new ClassifierCompositeItemProcessor<>();

        // 2) Classifier 는 "현재 item 을 보고, 어떤 Processor 를 실행할지 선택" 하는 역할이다.
        // classify(item) 로 들어온 item 의 값(여기서는 id)을 기준으로 processorMap 에서 꺼내서 리턴한다.
        ProcessorClassifier<ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> classifier = new ProcessorClassifier<>();

        // 3) id 값에 따라 실행할 Processor 를 고른다.
        Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();
        processorMap.put(1, new CustomClassifierItemProcessor1());
        processorMap.put(2, new CustomClassifierItemProcessor2());
        processorMap.put(3, new CustomClassifierItemProcessor3());

        classifier.setProcessorMap(processorMap); // classifier 에 "조건별 Processor 목록" 세팅
        processor.setClassifier(classifier); // 최종적으로 ClassifierCompositeItemProcessor 에 classifier 를 연결한다.

        return processor;
    }

}