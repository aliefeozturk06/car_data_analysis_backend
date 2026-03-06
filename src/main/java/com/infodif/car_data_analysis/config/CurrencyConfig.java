package com.infodif.car_data_analysis.config;

import com.infodif.car_data_analysis.client.CurrencyClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.client.support.RestClientAdapter;

@Configuration
public class CurrencyConfig {

    @Bean
    public CurrencyClient currencyClient() {
        RestClient restClient = RestClient.create("https://api.frankfurter.app");
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CurrencyClient.class);
    }
}