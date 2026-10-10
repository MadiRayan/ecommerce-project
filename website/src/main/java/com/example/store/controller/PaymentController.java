package com.example.store.controller;

import com.example.store.model.OrderResponse;
import com.example.store.service.WooCommercePaymentService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final WooCommercePaymentService paymentService;


    public PaymentController(
            WooCommercePaymentService paymentService) {

        this.paymentService = paymentService;
    }


    @GetMapping("/payment/success")
    public String paymentSuccess(

            @RequestParam(required = false)
            Long orderId,

            HttpSession session,

            Model model) {


        if (orderId == null) {

            return "payment-cancel";
        }


        try {

            OrderResponse order =
                    paymentService.getOrder(orderId);


            if (order == null) {

                return "payment-cancel";
            }


            String status = order.getStatus();


            boolean paymentSuccessful =

                    "processing".equalsIgnoreCase(status)

                    ||

                    "completed".equalsIgnoreCase(status);


            if (!paymentSuccessful) {

                model.addAttribute(
                        "orderId",
                        orderId
                );

                return "payment-cancel";
            }


            /*
             * PAYMENT CONFIRMED BY WOOCOMMERCE.
             *
             * NOW IT IS SAFE TO CLEAR
             * THE SPRING BOOT CART.
             */

            session.removeAttribute("cart");


            model.addAttribute(
                    "orderId",
                    order.getId()
            );


            model.addAttribute(
                    "orderStatus",
                    order.getStatus()
            );


            return "payment-success";


        } catch (Exception e) {

            System.out.println(
                    "PAYMENT VERIFICATION ERROR: "
                            + e.getMessage()
            );

            return "payment-cancel";
        }
    }


    @GetMapping("/payment/cancel")
    public String paymentCancel() {

        /*
         * CART IS NOT CLEARED.
         */

        return "payment-cancel";
    }
}