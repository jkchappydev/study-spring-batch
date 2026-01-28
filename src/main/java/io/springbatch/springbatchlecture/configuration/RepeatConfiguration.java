package io.springbatch.springbatchlecture.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RepeatConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job repeatConfigurationJob() {
        return new JobBuilder("repeatConfigurationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(repeatConfigurationStep())
                .build();
    }

    @Bean
    public Step repeatConfigurationStep() {
        return new StepBuilder("repeatConfigurationStep", jobRepository)
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 3 ? null : "item" + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    RepeatTemplate repeatTemplate = new RepeatTemplate();

                    @Override
                    public String process(String item) throws Exception {
                        // repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3)); // 3번의 반복이 일어나면 종료 (총 9번)
                        // repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3000)); // 3초동안 계속 반복하다가, 3초가 지나면 종료 (총 9초)

                        // 여러 개의 CompletionPolicy 을 복합적으로 사용가능 하게 설정 (AND 조건이 아닌 OR 조건으로 실행됨)
                        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
                        CompletionPolicy[] completionPolicies = new CompletionPolicy[]{
                                new SimpleCompletionPolicy(3),
                                new TimeoutTerminationPolicy(3000)
                        };
                        compositeCompletionPolicy.setPolicies(completionPolicies);
                        repeatTemplate.setCompletionPolicy(compositeCompletionPolicy);

                        // repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler()); // 예외가 발생하더라도 3번까지는 반복이 계속되다가, 4번부터 예외 던짐

                        // RepeatTemplate 을 통해서 반복실행하는 RepeatCallback 인터페이스 구현
                        repeatTemplate.iterate(new RepeatCallback() {
                            // reader 에 의해 생성되는 item 이 3개(item1~item3)이고 각 item 처리(process)마다 RepeatTemplate 이 3번 반복되므로 doInIteration()은 총 9번 실행된다.
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                // 여기서 비즈니스 로직 구현
                                System.out.println("repeatTemplate is testing");
                                // throw new RuntimeException("Exception");
                                return RepeatStatus.CONTINUABLE;
                                // return RepeatStatus.FINISHED; // 곧바로 반복이 종료된다.
                            }
                        });

                        return item;
                    }
                })
                .writer(System.out::println)
                .build();
    }

    @Bean
    public ExceptionHandler simpleLimitExceptionHandler() {
        return new SimpleLimitExceptionHandler(3);
    }

}
