package io.springbatch.springbatchlecture.configuration;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class CustomJobParametersValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        // 파라미터의 검증은 첫번째로 job 이 수행되기 전에 jobRepository 의 기능이 시작하기 전에 한번하고
        // 실제 job 이 실행되기 전에도 한번더 한다.
        // 총 두번의 검증 단계를 거친다.
        if (parameters.getString("name") == null) {
            throw new JobParametersInvalidException("name parameter is not found");
        }
    }

}
