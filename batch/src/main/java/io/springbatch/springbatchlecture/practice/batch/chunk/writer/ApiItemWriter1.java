package io.springbatch.springbatchlecture.practice.batch.chunk.writer;

import io.springbatch.springbatchlecture.practice.batch.domain.ApiRequestVO;
import io.springbatch.springbatchlecture.practice.batch.domain.ApiResponseVO;
import io.springbatch.springbatchlecture.practice.service.AbstractApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
public class ApiItemWriter1 extends FlatFileItemWriter<ApiRequestVO> {

    private final AbstractApiService apiService;

    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {
        ApiResponseVO responseVO = apiService.service(chunk);
        System.out.println("responseVO = " + responseVO);

        // 응답 내용을 파일로 저장
        chunk.forEach(item -> item.setApiResponseVO(responseVO));

        super.setResource(new FileSystemResource("/Users/jeong-geunchan/IdeaProjects/study-spring-batch/batch/src/main/resources/product1.txt"));
        super.open(new ExecutionContext());
        super.setLineAggregator(new DelimitedLineAggregator<>());
        super.setAppendAllowed(true);
        super.write(chunk);
    }

}
