package io.springbatch.springbatchlecture.listener;

import org.springframework.batch.core.ItemReadListener;

public class CustomItemReadListener implements ItemReadListener {

    @Override
    public void beforeRead() {
        System.out.println(">>>> Before Read");
    }

    @Override
    public void afterRead(Object item) {
        System.out.println(">>>> After Read : " + item);
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println(">>>> On Read Error");
    }

}
