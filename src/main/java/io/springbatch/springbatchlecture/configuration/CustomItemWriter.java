package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CustomItemWriter implements ItemWriter<Customer> {

    @Override
    public void write(Chunk<? extends Customer> items) throws Exception {
        items.forEach(item -> System.out.println(item));
    }

}
