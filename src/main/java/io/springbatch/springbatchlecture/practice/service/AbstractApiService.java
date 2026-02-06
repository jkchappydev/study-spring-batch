package io.springbatch.springbatchlecture.practice.service;

import io.springbatch.springbatchlecture.practice.batch.domain.ApiInfo;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiRequestVO;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiResponseVO;
import org.springframework.batch.item.Chunk;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

// 각각의 API 마다 서버가 별도로 있다고 가정한다.
public abstract class AbstractApiService {

    public ApiResponseVO service(Chunk<? extends ApiRequestVO> apiRequest) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        // RestTemplate 통신 중 오류가 발생하면, 해당 오류를 처리할 수 있는 핸들러를 생성
        RestTemplate restTemplate = builder.errorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        }).build();

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ApiInfo apiInfo = ApiInfo.builder().apiRequestList(apiRequest).build();

        return doApiService(restTemplate, apiInfo);
    }

    protected abstract ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo);

}
