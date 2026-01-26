package io.springbatch.springbatchlecture.configuration;

import io.springbatch.springbatchlecture.domain.Customer7;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomRowMapper implements RowMapper<Customer7> {

    @Override
    public Customer7 mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer7(
                rs.getLong("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("birthdate"));
    }

}
