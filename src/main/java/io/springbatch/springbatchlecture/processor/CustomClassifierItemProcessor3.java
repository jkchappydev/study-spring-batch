package io.springbatch.springbatchlecture.processor;

import org.springframework.batch.item.ItemProcessor;

public class CustomClassifierItemProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    // ClassifierCompositeItemProcessor 에서 "id=3"로 분류된 Item 이 들어오면 여기 Processor 가 선택돼서 process() 가 실행된다.
    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        // 가공처리는 여기서 진행한다.
        System.out.println("CustomClassifierItemProcessor3 process");

        return item;
    }

}