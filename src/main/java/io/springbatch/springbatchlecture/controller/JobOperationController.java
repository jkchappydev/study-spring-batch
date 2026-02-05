package io.springbatch.springbatchlecture.controller;

import io.springbatch.springbatchlecture.domain.JobInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class JobOperationController {

    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final JobRegistry jobRegistry;

    @PostMapping("/batch/start")
    public String startBatch(@RequestBody JobInfo jobInfo) throws Exception {

        // 등록된 모든 Job을 '전부' 실행
        /*for (String jobName : jobRegistry.getJobNames()) {
            // Spring Batch 5.2 부터는 JobOperator.start() 가 String 이 아니라 Properties 를 받도록 바뀜.
            Properties params = new Properties();
            params.setProperty("id", jobInfo.getId());
            // 매 실행마다 유니크한 값 (중복 방지)
            params.setProperty("run.id", String.valueOf(System.currentTimeMillis()));
            jobOperator.start(jobName, params);
        }

        return "batch started";*/

        Properties params = new Properties();
        params.setProperty("id", jobInfo.getId());
        params.setProperty("requestTime(long)", String.valueOf(System.currentTimeMillis()));

        Long executionId = jobOperator.start(jobInfo.getJobName(), params);
        return "batch started. executionId=" + executionId;
    }

    @PostMapping("/batch/stop")
    public String stopBatch(@RequestBody JobInfo jobInfo) throws Exception {
        // 등록된 모든 Job을 '전부' 종료
        /*for (String jobName : jobRegistry.getJobNames()) {
            // 현재 실행 중인 Job 들의 이름을 가져온다.
            Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(jobName);
            JobExecution jobExecution = runningJobExecutions.iterator().next();

            // 해당 api 를 호출한다고 바로 Job 이 중단되는 것이 아니고, 실행 중인 Step 있으면 해당 Step 까지는 실행을 완료한 이후에 종료한다.
            jobOperator.stop(jobExecution.getId());
        }*/

        // 현재 실행 중인 Job 들의 이름을 가져온다.
        Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(jobInfo.getJobName());
        if (runningJobExecutions.isEmpty()) {
            return "no running job";
        }

        JobExecution jobExecution = runningJobExecutions.iterator().next();

        // 해당 api 를 호출한다고 바로 Job 이 중단되는 것이 아니고, 실행 중인 Step 있으면 해당 Step 까지는 실행을 완료한 이후에 종료한다.
        jobOperator.stop(jobExecution.getId());
        return "stop requested. executionId=" + jobExecution.getId();
    }


    @PostMapping("/batch/restart")
    public String restartBatch(@RequestBody JobInfo jobInfo) throws Exception {

        String jobName = jobInfo.getJobName();

        JobInstance lastJobInstance = jobExplorer.getLastJobInstance(jobName);
        if (lastJobInstance == null) {
            return "no job instance";
        }

        JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
        if (lastJobExecution == null) {
            return "no job execution";
        }

        // 아직 STOPPING/STARTED면 restart 불가
        if (lastJobExecution.isRunning() || lastJobExecution.getStatus().name().equals("STOPPING")) {
            return "still running/stopping. executionId=" + lastJobExecution.getId();
        }

        Long newExecutionId = jobOperator.restart(lastJobExecution.getId());
        return "batch restarted. executionId=" + newExecutionId;
    }

}
