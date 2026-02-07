package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class CustomItemReader implements ItemReader<Customer> {

    private List<Customer> list;

    public CustomItemReader(List<Customer> list) {
        this.list = new ArrayList<>(list);
    }

    @Override
    public Customer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (!list.isEmpty()) {
            return list.remove(0); // 한 건씩 읽을 때 마다 헤당 데이터 list 에서 제거
        }

        return null; // null 을 반환하면 더 이상 읽을 아이템이 없다는 의미
    }

}
