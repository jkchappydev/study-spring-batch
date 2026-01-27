package io.springbatch.springbatchlecture.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

// 실제 ItemProcessor 을 선택한다. (1, 2, 3 중 선택한다.)
public class ProcessorClassifier<C, T> implements Classifier<C, T> {

    // 분기 테이블 역할
    // key(id 값) -> value(실행할 ItemProcessor)
    private Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();

    // classify() 로 넘어오는 객체(여기서는 ProcessorInfo)를 보고 어떤 Processor 를 실행할지 결정한다.
    // 리턴된 Processor 가 ClassifierCompositeItemProcessor 에 의해 실제로 실행된다.
    @Override
    public T classify(C classfiable) {
        return (T) processorMap.get(((ProcessorInfo) classfiable).getId()); // ProcessorInfo 에서 Id 를 가져온다.
    }

    public void setProcessorMap(Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap) {
        this.processorMap = processorMap;
    }

}