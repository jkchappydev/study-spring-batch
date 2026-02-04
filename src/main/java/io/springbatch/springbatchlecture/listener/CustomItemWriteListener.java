package io.springbatch.springbatchlecture.listener;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

public class CustomItemWriteListener implements ItemWriteListener {

    @Override
    public void beforeWrite(Chunk items) {
        System.out.println(">>>>>>>> Before Write");
    }

    @Override
    public void afterWrite(Chunk items) {
        System.out.println(">>>>>>>> After Write");
    }

    @Override
    public void onWriteError(Exception exception, Chunk items) {
        System.out.println(">>>>>>>> On Write Error");
    }

}
