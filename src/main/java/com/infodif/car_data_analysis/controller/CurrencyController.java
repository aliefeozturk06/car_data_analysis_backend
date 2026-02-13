package com.infodif.car_data_analysis.controller;

import com.infodif.car_data_analysis.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/rates")
    public Map<String, Double> getRates() {
        log.info("Fetching current currency exchange rates.");
        return currencyService.getExchangeRates();
    }
}