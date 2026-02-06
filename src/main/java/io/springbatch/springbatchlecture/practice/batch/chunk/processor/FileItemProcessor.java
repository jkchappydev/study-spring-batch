package io.springbatch.springbatchlecture.practice.batch.chunk.processor;

import io.springbatch.springbatchlecture.practice.batch.domain.Product;
import io.springbatch.springbatchlecture.practice.batch.domain.ProductVO;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {

    @Override
    public Product process(ProductVO item) throws Exception {
        // ItemReader 로 부터 ProductVO 를 전달받고, ItemWriter 로는 Product 타입으로 전달해야 하기 때문에 변환 작업이 필요하다.
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(item, Product.class);
    }

}
