package io.springbatch.springbatchlecture;

import io.springbatch.springbatchlecture.configuration.SpringBatchTestConfiguration;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;

import java.util.Date;
import java.util.List;

// @RunWith(SpringRunner.class) // JUnit4 전용. Spring Boot 3.x 는 JUnit5 가 기본
@SpringBatchTest // Batch 테스트 유틸(JobLauncherTestUtils 등) 제공
@SpringBootTest(classes={SpringBatchTestConfiguration.class, TestBatchConfig.class})
@RequiredArgsConstructor
// JUnit5는 기본적으로 생성자 파라미터를 스프링 빈으로 자동 주입하지 않는다.
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // JUnit5 에서 생성자 파라미터 스프링 주입 허용
public class StudySpringBatchApplicationTests {

    private final JobLauncherTestUtils jobLauncherTestUtils;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from customer2"); // 매 테스트 전 데이터 초기화
    }

    @Test
    public void springBatch_test() throws Exception {
        // given: Job 파라미터(동일 파라미터 재실행 방지용 date 포함)
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "user1")
                .addLong("date", new Date().getTime())
                .toJobParameters();

        // when: Job 실행
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        jdbcTemplate.update("delete from customer2"); // Step 단독 실행 검증을 위해 다시 초기화

        // when: 특정 Step 만 단독 실행
        JobExecution jobExecution1 = jobLauncherTestUtils.launchStep("springBatchTestConfigurationStep1");

        // then: Job 종료 상태 검증
        Assertions.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        // then: Step 실행 결과(커밋/읽기/쓰기 카운트) 검증
        StepExecution stepExecution = (StepExecution) ((List) jobExecution1.getStepExecutions()).get(0);
        Assertions.assertEquals(8, stepExecution.getCommitCount());
        Assertions.assertEquals(770, stepExecution.getReadCount());
        Assertions.assertEquals(770, stepExecution.getWriteCount());
    }

}
