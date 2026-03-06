package com.infodif.car_data_analysis.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;

@HttpExchange("https://api.frankfurter.app")
public interface CurrencyClient {

    @GetExchange("/latest")
    JsonNode getLatestRates(@RequestParam("from") String from);
}