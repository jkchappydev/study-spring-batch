package io.springbatch.springbatchlecture.practice.batch.chunk.writer;

import io.springbatch.springbatchlecture.practice.batch.domain.ApiRequestVO;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiResponseVO;
import io.springbatch.springbatchlecture.practice.service.AbstractApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
public class ApiItemWriter2 implements ItemWriter<ApiRequestVO> {

    private final AbstractApiService apiService;

    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {
        ApiResponseVO responseVO = apiService.service(chunk);
        System.out.println("responseVO = " + responseVO);
    }
    
}
