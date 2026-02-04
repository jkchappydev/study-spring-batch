package io.springbatch.springbatchlecture.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

import java.time.Duration;
import java.time.LocalDateTime;

public class JobAndStepAnnotationJobExecutionListener {

    // 이 때, 꼭 메서드명이 beforeJob, afterJob 일 필요는 없다.
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job is Started");
        System.out.println("Job Name: " + jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();
        long timeMs = Duration.between(start, end).toMillis();

        System.out.println("총 소요시간(ms) : " + timeMs);
    }

}
