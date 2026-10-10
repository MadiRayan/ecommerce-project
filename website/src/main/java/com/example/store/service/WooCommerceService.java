package com.example.store.service;

import com.example.store.model.Product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WooCommerceService {

    private final RestClient restClient;

    public WooCommerceService(
            @Value("${woocommerce.url}") String wooCommerceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(
                    wooCommerceUrl + "/wp-json/wc/store/v1"
                )
                .build();
    }

    public Product[] getProducts() {

        return restClient
                .get()
                .uri("/products?per_page=20")
                .retrieve()
                .body(Product[].class);
    }

    public Product getProduct(Long id) {

        return restClient
                .get()
                .uri("/products/" + id)
                .retrieve()
                .body(Product.class);
    }
}