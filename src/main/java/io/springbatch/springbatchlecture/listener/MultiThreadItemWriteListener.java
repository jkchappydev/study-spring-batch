package io.springbatch.springbatchlecture.listener;

import io.springbatch.springbatchlecture.test.Customer;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

public class MultiThreadItemWriteListener implements ItemWriteListener<Customer> {

    @Override
    public void beforeWrite(Chunk<? extends Customer> items) {
    }

    @Override
    public void afterWrite(Chunk<? extends Customer> items) {
        // 어떤 스레드가 어떤 아이템을 write 하고 있는지 확인
        System.out.println("Thread : " + Thread.currentThread().getName() + ", write items : " + items.size());
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends Customer> items) {
    }

}
