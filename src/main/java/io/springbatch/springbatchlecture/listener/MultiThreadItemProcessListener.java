package io.springbatch.springbatchlecture.listener;

import io.springbatch.springbatchlecture.test.Customer;
import org.springframework.batch.core.ItemProcessListener;

public class MultiThreadItemProcessListener implements ItemProcessListener<Customer, Customer> {

    @Override
    public void beforeProcess(Customer item) {
    }

    @Override
    public void afterProcess(Customer item, Customer result) {
        // 어떤 스레드가 어떤 아이템을 process 하고 있는지 확인
        System.out.println("Thread : " + Thread.currentThread().getName() + ", process item : " + item.getId());
    }

    @Override
    public void onProcessError(Customer item, Exception e) {
    }

}
