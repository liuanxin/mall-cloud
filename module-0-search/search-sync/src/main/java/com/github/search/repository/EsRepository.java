package com.github.search.repository;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EsRepository {

    @Autowired
    private RestHighLevelClient client;
}
