package io.springbatch.springbatchlecture.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalDateTime;

public class StopWatchJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();
        long timeMs = Duration.between(start, end).toMillis();

        System.out.println("========================");
        System.out.println("총 소요시간(ms) : " + timeMs);
        System.out.println("========================");
    }

}
