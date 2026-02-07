package io.springbatch.batchapiservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {

    // 포트가 다른 3개의 서버가 기동된다고 가정한다.
    // 8081
    @PostMapping("/api/product/1")
    public String product1(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> productVOList = apiInfo.getApiRequestList().stream()
                .map(item -> item.getProductVO())
                .collect(Collectors.toList());
        System.out.println("productVOList = " + productVOList);

        return "product1 was successfully processed";
    }

    // 8082
    @PostMapping("/api/product/2")
    public String product2(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> productVOList = apiInfo.getApiRequestList().stream()
                .map(item -> item.getProductVO())
                .collect(Collectors.toList());
        System.out.println("productVOList = " + productVOList);

        return "product2 was successfully processed";
    }

    // 8083
    @PostMapping("/api/product/3")
    public String product3(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> productVOList = apiInfo.getApiRequestList().stream()
                .map(item -> item.getProductVO())
                .collect(Collectors.toList());
        System.out.println("productVOList = " + productVOList);

        return "product3 was successfully processed";
    }

}
