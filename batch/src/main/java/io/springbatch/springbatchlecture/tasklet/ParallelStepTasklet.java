package io.springbatch.springbatchlecture.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class ParallelStepTasklet implements Tasklet {

    private long sum;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        for (int i = 0; i < 1000000000; i++) {
            sum++;
        }

        System.out.printf("%s han been executed on thread %s%n", chunkContext.getStepContext().getStepName(), Thread.currentThread().getName());
        System.out.printf("sum: %d%n", sum);

        return RepeatStatus.FINISHED;
    }

}
