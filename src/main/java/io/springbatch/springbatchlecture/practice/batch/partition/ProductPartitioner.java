package io.springbatch.springbatchlecture.practice.batch.partition;

import io.springbatch.springbatchlecture.practice.batch.domain.ProductVO;
import io.springbatch.springbatchlecture.practice.batch.job.api.QueryGenerator;
import lombok.Setter;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Setter
public class ProductPartitioner implements Partitioner {

    private DataSource dataSource;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        ProductVO[] productList = QueryGenerator.getProductList(dataSource);
        Map<String, ExecutionContext> result = new HashMap<>();

        int number = 0;

        // partition 을 몇 개 만들지를 결정해서, 각 partition 에 전달할 데이터를 ExecutionContext 에 담아 Map 으로 리턴
        for (ProductVO productVO : productList) {
            ExecutionContext value = new ExecutionContext();

            result.put("partition" + number, value);  // partition0, partition1, ...
            value.put("product", productVO); // partition 이 처리 대상 상품을 ExecutionContext 에 저장한다.

            number++;
        }

        // 결과적으로 상품 한개당 하나의 partition 이 만들어 진다.
        return result;
    }

}
