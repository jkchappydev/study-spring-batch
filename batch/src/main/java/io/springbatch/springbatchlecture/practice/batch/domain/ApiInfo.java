package io.springbatch.springbatchlecture.practice.batch.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.batch.item.Chunk;

import java.util.List;

@Data
@Builder
public class ApiInfo {

    private String url;
    private List<? extends ApiRequestVO> apiRequestList;

}
