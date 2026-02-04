package io.springbatch.springbatchlecture.listener;

import org.springframework.batch.core.ItemProcessListener;

public class CustomItemProcessListener implements ItemProcessListener {

    @Override
    public void beforeProcess(Object item) {
        System.out.println(">>>>>> Before Process : " + item);
    }

    @Override
    public void afterProcess(Object item, Object result) {
        System.out.println(">>>>>> After Process : " + item);
    }

    @Override
    public void onProcessError(Object item, Exception e) {
        System.out.println(">>>>>> On Process Error : " + item);
    }

}
