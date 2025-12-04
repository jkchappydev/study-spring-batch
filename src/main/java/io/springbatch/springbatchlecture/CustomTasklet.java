package io.springbatch.springbatchlecture;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

// Tasklet 를 커스텀하게 직접 구현할 수도 있다.
// @Component // 직접 빈으로 설정해서 다른 객체를 의존성 주입을 받을 수도 있다.
public class CustomTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 비즈니스 로직 구현
        System.out.println("step1 was executed");
        return RepeatStatus.FINISHED;
    }

}
