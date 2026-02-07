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
public class FileJobRunner extends JobRunner {

    private final Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) throws SchedulerException {
        // --job.name=apiJob requestDate=20260205
        // 여기서는 실행할 때 20260205 만 넘긴다.
        String[] sourcesArgs = args.getSourceArgs(); // Application 실행 시 전달한 원본 커맨드라인 인자 목록(String[])을 가져온다.

        // JobDetail : Quartz 가 실행할 Job 에 대한 상세 정보를 담고 있는 클래스
        JobDetail jobDetail = buildJobDetail(FileScheduleJob.class, "fileJob", "batch", new HashMap<>());

        // JobDataMap : 스케줄러가 Job 실행 시 함께 전달하는 Key-Value 저장소(배치의 JobParameters 처럼 동작)라서,
        // ApiScheduleJob 내부에서 "requestDate" 키로 값을 꺼내서 JobParameters 로 넘길 수 있다.
        jobDetail.getJobDataMap().put("requestDate", sourcesArgs[0]); // 해당 데이터가 배치까지 전달되어야 한다. (null 처리도 해야한다.)

        // Trigger : jobDetail 을 Quartz 스케줄러가 어떤 주기로 실행할 지 정하는 클래스 (여기서는 매 50초)
        Trigger trigger = buildJobTrigger("0/50 * * * * ?");

        scheduler.scheduleJob(jobDetail, trigger);
    }

}
