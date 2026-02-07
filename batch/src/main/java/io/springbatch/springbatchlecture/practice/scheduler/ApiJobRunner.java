package io.springbatch.springbatchlecture.practice.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class ApiJobRunner extends JobRunner {

    private final Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) throws SchedulerException {
        // JobDetail : Quartz 가 실행할 Job 에 대한 상세 정보를 담고 있는 클래스
        JobDetail jobDetail = buildJobDetail(ApiScheduleJob.class, "apiJob", "batch", new HashMap<>());
        // Trigger : jobDetail 을 Quartz 스케줄러가 어떤 주기로 실행할 지 정하는 클래스 (여기서는 매 30초)
        Trigger trigger = buildJobTrigger("0/30 * * * * ?");

        scheduler.scheduleJob(jobDetail, trigger);
    }

}
