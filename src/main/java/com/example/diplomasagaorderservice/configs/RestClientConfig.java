package com.example.diplomasagaorderservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

@Configuration
public class RestClientConfig {

    @Value("${services.payment.uri}")
    private String paymentUri;

    @Value("${services.delivery.uri}")
    private String deliveryUri;

    @Bean("restPayment")
    public RestTemplate restTemplatePayment() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        UriTemplateHandler uriTemplateHandler = new RootUriTemplateHandler(paymentUri);

        return restTemplateBuilder.uriTemplateHandler(uriTemplateHandler).build();
    }

    @Bean("restDelivery")
    public RestTemplate restTemplateDelivery() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        UriTemplateHandler uriTemplateHandler = new RootUriTemplateHandler(deliveryUri);

        return restTemplateBuilder.uriTemplateHandler(uriTemplateHandler).build();

    }

}
