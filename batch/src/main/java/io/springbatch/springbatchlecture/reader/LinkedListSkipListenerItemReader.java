package io.springbatch.springbatchlecture.reader;

import io.springbatch.springbatchlecture.exception.CustomSkipException;
import jakarta.annotation.Nullable;
import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;

import java.util.LinkedList;
import java.util.List;

public class LinkedListSkipListenerItemReader<T> implements ItemReader<T> {

    private List<T> list;

    public LinkedListSkipListenerItemReader(List<T> list) {
        if (AopUtils.isAopProxy(list)) {
            this.list = list;
        } else {
            this.list = new LinkedList<>(list);
        }
    }

    @Nullable
    @Override
    public T read() throws CustomSkipException {
        if (!list.isEmpty()) {
            T remove = (T) list.remove(0);
            if ((Integer) remove == 3) { // 한 건씩 읽은 데이터가 3 이면 예외 발생되도록 함
                throw new CustomSkipException("read skipped : " + remove);
            }
            System.out.println("read : " + remove);
            return remove;
        }
        return null;
    }

}
