package io.springbatch.springbatchlecture.controller;

import io.springbatch.springbatchlecture.domain.Member;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class JobLauncherController {

    private final Job syncJob;
    private final JobLauncher syncJobLauncher;
    private final Job asyncJob;
    private final JobLauncher asyncJobLauncher;

    // 현재 프로젝트에 Job 빈이 여러 개라서, 어떤 Job 을 주입할지 @Qualifier 로 지정해야 한다.
    // 그런데 Lombok(@RequiredArgsConstructor)은 필드의 @Qualifier 를 생성자 파라미터로 복사하지 않는 경우가 있어서,
    // 생성자를 직접 만들어서 @Qualifier 를 파라미터에 명시했다.
    public JobLauncherController(
            @Qualifier("syncJobLauncherJob") Job syncJob,
            @Qualifier("syncJobLauncher") JobLauncher syncJobLauncher,
            @Qualifier("asyncJobLauncherJob") Job asyncJob,
            @Qualifier("asyncJobLauncher") JobLauncher asyncJobLauncher
    ) {
        this.syncJob = syncJob;
        this.syncJobLauncher = syncJobLauncher;
        this.asyncJob = asyncJob;
        this.asyncJobLauncher = asyncJobLauncher;
    }

    @PostMapping("/syncBatch")
    public String launchSync(@RequestBody Member member) throws Exception {
        // 아무 간단한 파라미터(ex: Member 의 id)를 클라이언트로 부터 전달받아서, 해당 값을 JobParameters 로 사용한다.
        JobParameters params = new JobParametersBuilder()
                .addString("id", member.getId())
                .addDate("date", new Date())
                .toJobParameters();

        syncJobLauncher.run(syncJob, params);

        return "sync success";
    }

    @PostMapping("/asyncBatch")
    public String launchAsync(@RequestBody Member member) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("id", member.getId())
                .addDate("date", new Date())
                .toJobParameters();

        asyncJobLauncher.run(asyncJob, jobParameters);

        return "async success";
    }

}
