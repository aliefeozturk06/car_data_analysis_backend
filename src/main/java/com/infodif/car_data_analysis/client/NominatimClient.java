package com.infodif.car_data_analysis.client;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;

@HttpExchange("/search")
public interface NominatimClient {
    @GetExchange
    JsonNode search(
            @RequestParam("q") String query,
            @RequestParam("format") String format,
            @RequestParam("limit") int limit
    );
}