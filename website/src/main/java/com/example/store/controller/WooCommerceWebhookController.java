package com.example.store.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.example.store.service.OrderDatabaseService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WooCommerceWebhookController {

    private final String webhookSecret;

    private final OrderDatabaseService
            orderDatabaseService;


    public WooCommerceWebhookController(

            @Value("${woocommerce.webhook-secret}")
            String webhookSecret,

            OrderDatabaseService
            orderDatabaseService) {


        this.webhookSecret =
                webhookSecret;


        this.orderDatabaseService =
                orderDatabaseService;
    }


    /*
     * TEST ENDPOINT
     */

    @GetMapping("/webhooks/test")
    public ResponseEntity<String> testWebhook() {

        return ResponseEntity.ok(
                "Webhook controller is working"
        );
    }


    /*
     * WOOCOMMERCE ORDER WEBHOOK
     */

    @PostMapping("/webhooks/woocommerce/order")
    public ResponseEntity<String> receiveOrderWebhook(

            @RequestBody
            byte[] body,

            @RequestHeader(
                    value =
                            "X-WC-Webhook-Signature",

                    required =
                            false
            )
            String receivedSignature) {


        /*
         * VERIFY SECURITY
         */

        if (!isValidSignature(
                body,
                receivedSignature)) {


            System.out.println();

            System.out.println(
                    "INVALID WOOCOMMERCE WEBHOOK"
            );

            System.out.println();


            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            "Invalid webhook signature"
                    );
        }


        /*
         * PRINT WEBHOOK
         */

        String json =
                new String(
                        body,
                        StandardCharsets.UTF_8
                );


        System.out.println();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "VALID WOOCOMMERCE WEBHOOK RECEIVED"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                json
        );


        /*
         * SAVE / UPDATE DATABASE
         */

        orderDatabaseService
                .saveOrUpdateOrder(
                        body
                );


        return ResponseEntity.ok(
                "Webhook received successfully"
        );
    }


    /*
     * SIGNATURE VALIDATION
     */

    private boolean isValidSignature(

            byte[] body,

            String receivedSignature) {


        if (receivedSignature == null
                ||
                receivedSignature.isBlank()) {

            return false;
        }


        try {


            Mac mac =
                    Mac.getInstance(
                            "HmacSHA256"
                    );


            SecretKeySpec secretKey =
                    new SecretKeySpec(

                            webhookSecret
                                    .getBytes(
                                            StandardCharsets.UTF_8
                                    ),

                            "HmacSHA256"
                    );


            mac.init(
                    secretKey
            );


            byte[] calculatedBytes =
                    mac.doFinal(
                            body
                    );


            String calculatedSignature =
                    Base64
                            .getEncoder()
                            .encodeToString(
                                    calculatedBytes
                            );


            return MessageDigest.isEqual(

                    calculatedSignature
                            .getBytes(
                                    StandardCharsets.UTF_8
                            ),

                    receivedSignature
                            .getBytes(
                                    StandardCharsets.UTF_8
                            )
            );


        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}