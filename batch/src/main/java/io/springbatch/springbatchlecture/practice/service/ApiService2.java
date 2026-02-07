package io.springbatch.springbatchlecture.practice.service;

import io.springbatch.springbatchlecture.practice.batch.domain.ApiInfo;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// 서버 2 이라고 가정한다.
@Service
public class ApiService2 extends AbstractApiService {

    private static final String SERVICE_API = "http://localhost:8082/api/product/2";

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
