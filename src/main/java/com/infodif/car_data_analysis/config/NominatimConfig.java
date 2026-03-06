package com.infodif.car_data_analysis.config;

import com.infodif.car_data_analysis.client.NominatimClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class NominatimConfig {

    @Bean
    public NominatimClient nominatimClient() {
        RestClient restClient = RestClient.create("https://nominatim.openstreetmap.org");
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(NominatimClient.class);
    }
}