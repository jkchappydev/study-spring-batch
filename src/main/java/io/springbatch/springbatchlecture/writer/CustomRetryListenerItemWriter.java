package io.springbatch.springbatchlecture.writer;

import io.springbatch.springbatchlecture.exception.CustomRetryException;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CustomRetryListenerItemWriter implements ItemWriter<String> {

    int count = 0;

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        for (String item : chunk) {
            if (count < 2) {
                if (count % 2 == 0) {
                    count++;
                } else if (count % 2 == 1) {
                    count++;
                    throw new CustomRetryException("failed");
                }
            }
            System.out.println("write : " + item);
        }
    }

}
