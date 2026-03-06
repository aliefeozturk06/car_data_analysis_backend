package com.infodif.car_data_analysis.service;

import com.infodif.car_data_analysis.client.CurrencyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyClient currencyClient;

    public Map<String, Double> getExchangeRates() {
        Map<String, Double> rates = new HashMap<>();

        try {
            JsonNode response = currencyClient.getLatestRates("TRY");

            if (response != null && response.has("rates")) {
                JsonNode ratesNode = response.get("rates");

                if (ratesNode.has("USD")) rates.put("USD", ratesNode.get("USD").asDouble());
                if (ratesNode.has("EUR")) rates.put("EUR", ratesNode.get("EUR").asDouble());
                if (ratesNode.has("GBP")) rates.put("GBP", ratesNode.get("GBP").asDouble());

                rates.put("TRY", 1.0);
                log.info("Exchange rates updated successfully.");
            }
        } catch (Exception e) {
            log.error("LIVE RATE ERROR: {}. Activating fallback rates.", e.getMessage());

            rates.put("TRY", 1.0);
            rates.put("USD", 0.031);
            rates.put("EUR", 0.029);
            rates.put("GBP", 0.024);
        }

        return rates;
    }
}