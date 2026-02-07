package io.springbatch.springbatchlecture.template;

import io.springbatch.springbatchlecture.exception.RetryableException;
import io.springbatch.springbatchlecture.test.Customer3;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

@RequiredArgsConstructor
public class RetryItemProcessor2 implements ItemProcessor<String, Customer3> {

    @Autowired
    private RetryTemplate retryTemplate;

    private int cnt;

    @Override
    public Customer3 process(String item) throws Exception {

        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(true); // 예외 대상 기준이 없으면 기본으로 롤백(true) 처리한다.

        // RetryTemplate 는 RetryCallback, RecoveryCallback 두 개의 클래스를 실행하게 되고,
        // 해당 클래스 안에서 ItemProcessor 와 ItemWriter 를 실행하게 된다.
        Customer3 customer3 = retryTemplate.execute(new RetryCallback<Customer3, RetryableException>() {
            @Override
            public Customer3 doWithRetry(RetryContext retryContext) throws RetryableException {
                if (item.equals("1") || item.equals("2")) {
                    cnt++;
                    throw new RetryableException("failed cnt : " + cnt);
                }
                return new Customer3(item);
            }
        }, new RecoveryCallback<Customer3>() {
            @Override
            public Customer3 recover(RetryContext retryContext) throws Exception {
                return new Customer3(item);
            }
        }, new DefaultRetryState(item, rollbackClassifier));
        // new DefaultRetryState(item, rollbackClassifier) -> “item 기준으로 retry 상태를 유지하고, 예외가 나면 rollbackClassifier 판단에 따라 롤백 처리할지 결정한다.”
        return customer3;
    }

}
