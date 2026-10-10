package com.example.store.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.store.model.CartItem;
import com.example.store.model.OrderResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WooCommerceOrderService {

    private final RestClient restClient;


    public WooCommerceOrderService(

            @Value("${woocommerce.url}")
            String wooCommerceUrl,

            @Value("${woocommerce.consumer-key}")
            String consumerKey,

            @Value("${woocommerce.consumer-secret}")
            String consumerSecret) {


        this.restClient = RestClient
                .builder()

                .baseUrl(
                        wooCommerceUrl
                        + "/wp-json/wc/v3"
                )

                .defaultHeaders(
                        headers ->
                                headers.setBasicAuth(
                                        consumerKey,
                                        consumerSecret
                                )
                )

                .build();
    }


    public OrderResponse createOrder(

            List<CartItem> cart,

            String firstName,
            String lastName,
            String email,
            String phone,
            String address,
            String city,
            String country,

            String paymentMethod) {


        /*
         * BILLING
         */

        Map<String, Object> billing =
                new HashMap<>();


        billing.put(
                "first_name",
                firstName
        );

        billing.put(
                "last_name",
                lastName
        );

        billing.put(
                "email",
                email
        );

        billing.put(
                "phone",
                phone
        );

        billing.put(
                "address_1",
                address
        );

        billing.put(
                "city",
                city
        );

        billing.put(
                "country",
                country
        );


        /*
         * SHIPPING
         */

        Map<String, Object> shipping =
                new HashMap<>();


        shipping.put(
                "first_name",
                firstName
        );

        shipping.put(
                "last_name",
                lastName
        );

        shipping.put(
                "address_1",
                address
        );

        shipping.put(
                "city",
                city
        );

        shipping.put(
                "country",
                country
        );


        /*
         * PRODUCTS
         */

        List<Map<String, Object>> lineItems =
                new ArrayList<>();


        for (CartItem item : cart) {

            Map<String, Object> lineItem =
                    new HashMap<>();


            lineItem.put(
                    "product_id",
                    item.getProduct().getId()
            );


            lineItem.put(
                    "quantity",
                    item.getQuantity()
            );


            lineItems.add(lineItem);
        }


        /*
         * ORDER DATA
         */

        Map<String, Object> orderData =
                new HashMap<>();


        orderData.put(
                "status",
                "pending"
        );


        orderData.put(
                "billing",
                billing
        );


        orderData.put(
                "shipping",
                shipping
        );


        orderData.put(
                "line_items",
                lineItems
        );


        /*
         * CASH ON DELIVERY
         */

        if ("cod".equals(paymentMethod)) {

            orderData.put(
                    "payment_method",
                    "cod"
            );


            orderData.put(
                    "payment_method_title",
                    "Cash on Delivery"
            );


            orderData.put(
                    "set_paid",
                    false
            );
        }


        /*
         * STRIPE
         */

        if ("stripe".equals(paymentMethod)) {

            orderData.put(
                    "payment_method",
                    "stripe"
            );


            orderData.put(
                    "payment_method_title",
                    "Credit / Debit Card"
            );


            orderData.put(
                    "set_paid",
                    false
            );
        }


        /*
         * CREATE ORDER IN WOOCOMMERCE
         */

        return restClient
                .post()

                .uri("/orders")

                .body(orderData)

                .retrieve()

                .body(OrderResponse.class);
    }
}