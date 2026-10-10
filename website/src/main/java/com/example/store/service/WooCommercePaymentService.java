package com.example.store.service;

import com.example.store.model.OrderResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WooCommercePaymentService {

    private final RestClient restClient;


    public WooCommercePaymentService(

            @Value("${woocommerce.url}")
            String wooCommerceUrl,

            @Value("${woocommerce.consumer-key}")
            String consumerKey,

            @Value("${woocommerce.consumer-secret}")
            String consumerSecret) {


        this.restClient = RestClient.builder()

                .baseUrl(
                        wooCommerceUrl
                                + "/wp-json/wc/v3"
                )

                .defaultHeaders(headers ->
                        headers.setBasicAuth(
                                consumerKey,
                                consumerSecret
                        )
                )

                .build();
    }


    public OrderResponse getOrder(Long orderId) {

        return restClient
                .get()
                .uri("/orders/" + orderId)
                .retrieve()
                .body(OrderResponse.class);
    }
}