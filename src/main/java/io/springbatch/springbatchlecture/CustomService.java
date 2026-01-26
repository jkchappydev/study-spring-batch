package io.springbatch.springbatchlecture;

public class CustomService<T> {

    private int cnt = 0;
    private static final int MAX = 100;

    public T customRead() {
        if (cnt >= MAX) {
            return null;
        }
        return (T)("item" + cnt++);
    }

}
