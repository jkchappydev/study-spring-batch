package io.springbatch.springbatchlecture.practice.batch.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.batch.item.Chunk;

@Data
@Builder
public class ApiInfo {

    private String url;
    private Chunk<? extends ApiRequestVO> apiRequestList;

}
