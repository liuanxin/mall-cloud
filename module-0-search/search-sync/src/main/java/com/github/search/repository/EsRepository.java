package com.github.search.repository;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

@Component
public class EsRepository {

    private final RestHighLevelClient client;

    public EsRepository(RestHighLevelClient client) {
        this.client = client;
    }
}
