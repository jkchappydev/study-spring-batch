package io.springbatch.springbatchlecture.practice.batch.job.api;

import io.springbatch.springbatchlecture.practice.batch.domain.ProductVO;
import io.springbatch.springbatchlecture.practice.batch.rowmapper.ProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGenerator {

    public static ProductVO[] getProductList(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<ProductVO> productList = jdbcTemplate.query("select type from product group by type", new ProductRowMapper() {
            @Override // 여기서는 ProductVO 에서 type 만 가져올 거기 때문에 별도로 한번 더 정의
            public ProductVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                return ProductVO.builder()
                        .type(rs.getString("type"))
                        .build();
            }
        });

        return productList.toArray(new ProductVO[]{});
    }

    public static Map<String, Object> getParameterForQuery(String parameter, String value) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter, value);

        return parameters;
    }

}
