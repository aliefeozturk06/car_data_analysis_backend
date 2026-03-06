package com.infodif.car_data_analysis.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;

@HttpExchange("https://turkiyeapi.dev/api/v1")
public interface TurkiyeApiClient {

    @GetExchange("/provinces")
    JsonNode getAllProvinces();

    @GetExchange("/provinces/{id}")
    JsonNode getProvinceDetail(int id);
}