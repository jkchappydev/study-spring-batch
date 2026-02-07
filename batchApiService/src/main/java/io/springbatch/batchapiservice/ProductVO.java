package io.springbatch.batchapiservice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductVO {

    private Long id;
    private String name;
    private int price;
    private String type;

}
