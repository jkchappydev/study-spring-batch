package io.springbatch.springbatchlecture.rowmapper;

import io.springbatch.springbatchlecture.domain.Customer8;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcBatchRowMapper implements RowMapper<Customer8> {

    @Override
    public Customer8 mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer8(
                rs.getLong("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("birthdate"));
    }

}
