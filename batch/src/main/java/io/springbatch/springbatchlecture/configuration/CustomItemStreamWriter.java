package io.springbatch.springbatchlecture.configuration;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

public class CustomItemStreamWriter implements ItemStreamWriter<String> {

    @Override
    public void write(Chunk<? extends String> items) throws Exception {
        items.forEach(item -> System.out.println("item = " + item));
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("customItemStreamWriter open");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        System.out.println("customItemStreamWriter update");
    }

    @Override
    public void close() throws ItemStreamException {
        System.out.println("customItemStreamWriter close");
    }

}
