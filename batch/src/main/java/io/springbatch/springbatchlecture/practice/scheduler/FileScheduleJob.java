package io.springbatch.springbatchlecture.practice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FileScheduleJob extends QuartzJobBean {

    private final Job fileJob;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer; // 동일한 날짜에 파일을 두번 읽지 않게 하기 위해 추가

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) {
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

        // jobLauncher 에서 실제 Job 을 실행시키기 전, 같은 requestDate 로 중복 실행되지 않게 사전 검사한다.
        // jobExecutionId 를 가져와서, DB 에 같은 값이 있다면, 동일한 Job 이 실행되지 않도록 한다.
        // 이 때, getJobExecutions() 를 사용해서 모든 jobExecutionId 를 가져온다. 해당 메서드에는 인자로 JobInstance 가 필요하다.
        // 동일한 Job 이라도 여러개의 JobInstance 가 생성될 수 있기 때문에, 우선 Job 이름을 통해 해당 Job 이 몇개의 JobInstance 가 있는지 확인한다.
        long jobInstanceCount = jobExplorer.getJobInstanceCount(fileJob.getName()); // 동일 Job 이름으로 만들어진 JobInstance 가 총 몇 개인지 조회한다.
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(fileJob.getName(), 0, (int) jobInstanceCount); // 조회된 개수만큼 JobInstance 목록을 한 번에 가져온다. (0부터 count 까지)

        // JobInstance 가 하나라도 있으면, 각각의 실행 이력(JobExecution)을 더 확인한다.
        /*if (!jobInstances.isEmpty()) {
            // JobInstance 를 순회하면서 해당 인스턴스의 실행 기록(JobExecution)들을 가져온다.
            for (JobInstance jobInstance : jobInstances) {
                List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

                // 실행 기록 중에서, JobParameters 의 requestDate 가 현재 requestDate 와 같은 것만 걸러낸다.
                List<JobExecution> duplicatedExecutions = jobExecutions.stream()
                        .filter(jobExecution -> {
                            String executedRequestDate = jobExecution.getJobParameters().getString("requestDate"); // 해당 실행이 가진 requestDate
                            return Objects.equals(executedRequestDate, requestDate); // 지금 들어온 requestDate 와 비교
                        }) // 지금 들어온 requestDate 와 비교한다.
                        .toList(); // 조건에 맞는 실행들을 리스트로 만든다.

                // 같은 requestDate 로 실행된 이력이 있으면 중복이므로 예외로 실행을 막는다.
                if (!duplicatedExecutions.isEmpty()) {
                    throw new JobExecutionException(requestDate + " already exists");
                }
            }
        }*/

        // List 생성 없이 최적화
        // 같은 requestDate 로 이미 실행(또는 실행된 이력)이 있으면 중복 실행을 막는다.
        for (JobInstance jobInstance : jobInstances) {
            for (JobExecution jobExecution : jobExplorer.getJobExecutions(jobInstance)) {

                // JobExecution 의 requestDate 와 지금 실행하려는 requestDate 가 같으면 즉시 중단
                String executedRequestDate = jobExecution.getJobParameters().getString("requestDate");
                if (Objects.equals(executedRequestDate, requestDate)) {
                    throw new JobExecutionException(requestDate + " already exists");
                }
            }
        }

        // 최종적으로 Quartz 스케줄러가 "fileJob" 배치를, 위에서 만든 JobParameters 를 붙여 실행한다.
        jobLauncher.run(fileJob, jobParameters);
    }

}
