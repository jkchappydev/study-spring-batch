package io.springbatch.springbatchlecture.practice.batch.classifier;

import io.springbatch.springbatchlecture.practice.batch.domain.ApiRequestVO;
import io.springbatch.springbatchlecture.practice.batch.domain.ProductVO;
import lombok.Setter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

// 실제 ItemProcessor 을 선택한다. (type(상품타입) 1, 2, 3 중 선택한다.)
@Setter
public class ProcessorClassifier<C, T> implements Classifier<C, T> {

    // 분기 테이블 역할
    // key(id 값) -> value(실행할 ItemProcessor)
    private Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();

    // classify() 로 넘어오는 객체(여기서는 ProductVO 의 상품 타입)를 보고 어떤 Processor 를 실행할지 결정한다.
    // 리턴된 Processor 가 ClassifierCompositeItemProcessor 에 의해 실제로 실행된다.
    @Override
    public T classify(C classifiable) {
        return (T) processorMap.get(((ProductVO) classifiable).getType()); // ProductVO 에서 type(상품타입) 을 가져온다.
    }

}
