package com.infodif.car_data_analysis.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyService {

    private final String API_URL = "https://api.frankfurter.app/latest?from=TRY";

    public Map<String, Double> getExchangeRates() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Double> rates = new HashMap<>();

        try {
            Map response = restTemplate.getForObject(API_URL, Map.class);

            if (response != null && response.containsKey("rates")) {
                Map<String, Object> ratesData = (Map<String, Object>) response.get("rates");

                if (ratesData.containsKey("USD")) rates.put("USD", Double.valueOf(ratesData.get("USD").toString()));
                if (ratesData.containsKey("EUR")) rates.put("EUR", Double.valueOf(ratesData.get("EUR").toString()));
                if (ratesData.containsKey("GBP")) rates.put("GBP", Double.valueOf(ratesData.get("GBP").toString()));

                rates.put("TRY", 1.0);
            }
        } catch (Exception e) {
            System.err.println("CANLI KUR HATASI: " + e.getMessage());
            rates.put("TRY", 1.0);
            rates.put("USD", 0.032);
            rates.put("EUR", 0.029);
            rates.put("GBP", 0.025);
        }

        return rates;
    }
}