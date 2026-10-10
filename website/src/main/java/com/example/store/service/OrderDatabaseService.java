package com.example.store.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.store.entity.OrderEntity;
import com.example.store.repository.OrderRepository;

import tools.jackson.databind.json.JsonMapper;

@Service
public class OrderDatabaseService {

    private final OrderRepository orderRepository;

    private final JsonMapper jsonMapper;


    public OrderDatabaseService(

            OrderRepository orderRepository,

            JsonMapper jsonMapper) {

        this.orderRepository =
                orderRepository;

        this.jsonMapper =
                jsonMapper;
    }


    @SuppressWarnings("unchecked")
    public void saveOrUpdateOrder(
            byte[] webhookBody) {


        try {


            /*
             * CONVERT BODY INTO JSON
             */

            String json =
                    new String(
                            webhookBody,
                            StandardCharsets.UTF_8
                    );


            Map<String, Object> orderData =
                    jsonMapper.readValue(
                            json,
                            Map.class
                    );


            /*
             * WOOCOMMERCE ORDER ID
             */

            Number idNumber =
                    (Number)
                    orderData.get("id");


            if (idNumber == null) {

                return;
            }


            Long wooOrderId =
                    idNumber.longValue();


            /*
             * FIND EXISTING ORDER
             */

            OrderEntity order =
                    orderRepository
                            .findByWooCommerceOrderId(
                                    wooOrderId
                            )
                            .orElse(
                                    new OrderEntity()
                            );


            order.setWooCommerceOrderId(
                    wooOrderId
            );


            /*
             * ORDER NUMBER
             */

            Object number =
                    orderData.get("number");


            if (number != null) {

                order.setOrderNumber(
                        number.toString()
                );
            }


            /*
             * STATUS
             */

            Object status =
                    orderData.get("status");


            if (status != null) {

                order.setStatus(
                        status.toString()
                );
            }


            /*
             * TOTAL
             */

            Object total =
                    orderData.get("total");


            if (total != null) {

                order.setTotal(
                        new BigDecimal(
                                total.toString()
                        )
                );
            }


            /*
             * CURRENCY
             */

            Object currency =
                    orderData.get("currency");


            if (currency != null) {

                order.setCurrency(
                        currency.toString()
                );
            }


            /*
             * PAYMENT METHOD
             */

            Object paymentMethod =
                    orderData.get(
                            "payment_method_title"
                    );


            if (paymentMethod != null) {

                order.setPaymentMethod(
                        paymentMethod.toString()
                );
            }


            /*
             * BILLING CUSTOMER
             */

            Object billingObject =
                    orderData.get("billing");


            if (billingObject
                    instanceof Map<?, ?>) {


                Map<String, Object> billing =
                        (Map<String, Object>)
                                billingObject;


                Object firstName =
                        billing.get(
                                "first_name"
                        );


                Object lastName =
                        billing.get(
                                "last_name"
                        );


                Object email =
                        billing.get(
                                "email"
                        );


                if (firstName != null) {

                    order.setCustomerFirstName(
                            firstName.toString()
                    );
                }


                if (lastName != null) {

                    order.setCustomerLastName(
                            lastName.toString()
                    );
                }


                if (email != null) {

                    order.setEmail(
                            email.toString()
                    );
                }
            }


            /*
             * UPDATE TIME
             */

            order.setLastUpdated(
                    LocalDateTime.now()
            );


            /*
             * SAVE DATABASE RECORD
             */

            orderRepository.save(
                    order
            );


            System.out.println();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "ORDER SAVED TO DATABASE"
            );

            System.out.println(
                    "WooCommerce Order ID: "
                    + wooOrderId
            );

            System.out.println(
                    "Status: "
                    + order.getStatus()
            );

            System.out.println(
                    "========================================"
            );

            System.out.println();


        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}	