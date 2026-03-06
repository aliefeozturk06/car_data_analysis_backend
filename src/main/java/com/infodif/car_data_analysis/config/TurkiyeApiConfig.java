package com.infodif.car_data_analysis.config;

import com.infodif.car_data_analysis.client.TurkiyeApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.client.support.RestClientAdapter;

@Configuration
public class TurkiyeApiConfig {

    @Bean
    public TurkiyeApiClient turkiyeApiClient() {
        RestClient restClient = RestClient.create("https://turkiyeapi.dev/api/v1");

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(TurkiyeApiClient.class);
    }
}