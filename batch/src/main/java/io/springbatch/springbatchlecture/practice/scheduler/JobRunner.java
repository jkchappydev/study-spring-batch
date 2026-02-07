package io.springbatch.springbatchlecture.practice.scheduler;

import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Map;

// 애플리케이션 구동 시 Quartz 스케줄러에 JobDetail/Trigger 를 등록해서, 설정한 cron 주기대로 배치(Job)를 자동 실행시키는 클래스
public abstract class JobRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Program Arguments 에 지정한 날짜 데이터가 Application 구동 시에 여기로 오게 된다.
        // args 는 필요한 하위 클래스 (여기서는 FileJobRunner) 에서 사용한다.
        doRun(args);
    }

    protected abstract void doRun(ApplicationArguments args) throws SchedulerException;

    public JobDetail buildJobDetail(Class<? extends Job> job, String name, String group, Map<String, Object> params) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);

        return JobBuilder.newJob(job) // newJob() 에서 실제 job 을 생성
                .withIdentity(name, group)
                .usingJobData(jobDataMap)
                .build();
    }

    public Trigger buildJobTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp))
                .build();
    }

}
