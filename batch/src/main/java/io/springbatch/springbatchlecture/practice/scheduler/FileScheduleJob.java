package io.springbatch.springbatchlecture.practice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class FileScheduleJob extends QuartzJobBean {

    private final Job fileJob;
    private final JobLauncher jobLauncher;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Quartz 가 JobRunner(FileJobRunner)에서 JobDataMap 에 넣어둔 requestDate 값을 꺼내온다.
        // (Quartz 는 JobParameters 를 직접 넘기지 못하므로, JobDataMap 을 "중간 전달용 컨테이너"로 사용한다.)
        String requestDate = (String) context.getJobDetail().getJobDataMap().get("requestDate");

        // Quartz 트리거가 50초마다 실행되면 같은 파라미터로는 JobInstance 가 재사용되어 재실행/중복 실행 문제가 생길 수 있으니,
        // 매 실행마다 고유한 파라미터(id=현재시간)를 추가해서 JobInstance 를 새로 만들고,
        // requestDate 도 함께 전달해서 배치에서 @JobScope/@StepScope 로 사용할 수 있게 한다.
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("requestDate", requestDate)
                .toJobParameters();

        // 최종적으로 Quartz 스케줄러가 "fileJob" 배치를, 위에서 만든 JobParameters 를 붙여 실행한다.
        jobLauncher.run(fileJob, jobParameters);
    }

}
