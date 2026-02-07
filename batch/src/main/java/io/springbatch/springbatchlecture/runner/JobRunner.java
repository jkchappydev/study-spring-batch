package io.springbatch.springbatchlecture.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Spring Boot 가 초기화되고 완료가되면 가장 먼저 호출하는 타입의 클래스가 ApplicationRunner
@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher; // Job 을 실행시키는 클래스 (Batch 6.0 부터 사용 불가능)
    private final Job instanceJob; // 우리가 작성한 Job 를 의존성 주입받음

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user2") // user1 로 한번 더 실행하면 예외발생
                        .toJobParameters();

        // jobLauncher.run(instanceJob, jobParameters);
    }

}
