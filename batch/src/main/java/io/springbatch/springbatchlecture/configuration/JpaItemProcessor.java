package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.test.Customer;
import io.springbatch.springbatchlecture.test.Customer2;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class JpaItemProcessor implements ItemProcessor<Customer, Customer2> {

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public Customer2 process(Customer item) throws Exception {
        return modelMapper.map(item, Customer2.class); // 첫번째 인자 : 변환하고자 하는 item, 두번째 인자 : 변환하려는 Entity
    }

}
