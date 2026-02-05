package io.springbatch.springbatchlecture.reader;

import io.springbatch.springbatchlecture.exception.CustomRetryException;
import jakarta.annotation.Nullable;
import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;

import java.util.LinkedList;
import java.util.List;

public class LinkedListRetryListenerItemReader<T> implements ItemReader<T> {

    private List<T> list;

    public LinkedListRetryListenerItemReader(List<T> list) {
        if (AopUtils.isAopProxy(list)) {
            this.list = list;
        } else {
            this.list = new LinkedList<>(list);
        }
    }

    @Nullable
    @Override
    public T read() throws CustomRetryException {
        if (!list.isEmpty()) {
            return (T) list.remove(0);
        }
        return null;
    }

}
