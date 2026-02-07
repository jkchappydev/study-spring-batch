package io.springbatch.springbatchlecture.writer;

import io.springbatch.springbatchlecture.exception.SkippableException;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class SkipItemWriter implements ItemWriter<String> {

    private int cnt = 0;

    @Override
    public void write(Chunk<? extends String> items) throws Exception {

        for (String item : items) {
            if (item.equals("-12")) {
                System.out.println("\n==================== [SKIP @ WRITE] item=-12 ====================\n");
                throw new SkippableException("Write failed cnt : " + cnt);
            } else {
                System.out.println("ItemWriter : " + item);
            }
        }

    }

}
