package io.springbatch.springbatchlecture.practice.service;

import io.springbatch.springbatchlecture.practice.batch.domain.ApiInfo;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// 서버 1 이라고 가정한다.
@Service
public class ApiService1 extends AbstractApiService {

    private static final String SERVICE_API = "http://localhost:8081/api/product/1";

    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(SERVICE_API, apiInfo, String.class);
        int statusCodeValue = responseEntity.getStatusCode().value();

        return ApiResponseVO.builder()
                .status(statusCodeValue)
                .message(responseEntity.getBody())
                .build();
    }

}
