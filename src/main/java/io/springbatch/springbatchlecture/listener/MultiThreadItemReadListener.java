package io.springbatch.springbatchlecture.listener;

import io.springbatch.springbatchlecture.test.Customer;
import org.springframework.batch.core.ItemReadListener;

public class MultiThreadItemReadListener implements ItemReadListener<Customer> {

    @Override
    public void beforeRead() {
    }

    @Override
    public void afterRead(Customer item) {
        // ItemReader 가 데이터를 읽은 이후에 afterRead() 호출하고 읽은 item 을 인자로 전달함.
        // 어떤 스레드가 어떤 아이템을 read 하고 있는지 확인
        System.out.println("Thread : " + Thread.currentThread().getName() + ", read item : " + item.getId());
    }

    @Override
    public void onReadError(Exception ex) {
    }

}
