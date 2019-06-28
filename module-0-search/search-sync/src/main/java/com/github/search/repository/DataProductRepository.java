package com.github.search.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DataProductRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public DataProductRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate parameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.parameterJdbcTemplate = parameterJdbcTemplate;
    }
}
