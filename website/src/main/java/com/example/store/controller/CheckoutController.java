package com.example.store.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.store.model.CartItem;
import com.example.store.model.OrderResponse;
import com.example.store.service.WooCommerceOrderService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class CheckoutController {

    private final WooCommerceOrderService orderService;

    private final String wooCommerceUrl;


    public CheckoutController(

            WooCommerceOrderService orderService,

            @Value("${woocommerce.url}")
            String wooCommerceUrl) {

        this.orderService =
                orderService;

        this.wooCommerceUrl =
                wooCommerceUrl;
    }


    /*
     * SHOW CHECKOUT PAGE
     */

    @SuppressWarnings("unchecked")
    @GetMapping("/checkout")
    public String checkout(
            HttpSession session,
            Model model) {


        List<CartItem> cart =
                (List<CartItem>)
                        session.getAttribute("cart");


        if (cart == null) {

            cart = new ArrayList<>();
        }


        if (cart.isEmpty()) {

            return "redirect:/cart";
        }


        BigDecimal cartTotal =
                BigDecimal.ZERO;


        for (CartItem item : cart) {

            cartTotal =
                    cartTotal.add(
                            item.getTotal()
                    );
        }


        model.addAttribute(
                "cart",
                cart
        );


        model.addAttribute(
                "cartTotal",
                cartTotal
        );


        return "checkout";
    }


    /*
     * PLACE ORDER
     */

    @SuppressWarnings("unchecked")
    @PostMapping("/checkout/place-order")
    public String placeOrder(

            @RequestParam
            String firstName,

            @RequestParam
            String lastName,

            @RequestParam
            String email,

            @RequestParam
            String phone,

            @RequestParam
            String address,

            @RequestParam
            String city,

            @RequestParam
            String country,

            @RequestParam
            String paymentMethod,

            HttpSession session,

            Model model) {


        /*
         * GET CART
         */

        List<CartItem> cart =
                (List<CartItem>)
                        session.getAttribute("cart");


        if (cart == null ||
                cart.isEmpty()) {

            return "redirect:/cart";
        }


        /*
         * CREATE WOOCOMMERCE ORDER
         */

        OrderResponse order =
                orderService.createOrder(

                        cart,

                        firstName,
                        lastName,
                        email,
                        phone,
                        address,
                        city,
                        country,

                        paymentMethod
                );


        /*
         * STRIPE PAYMENT
         */

        if ("stripe".equals(paymentMethod)) {


            String paymentUrl =
                    UriComponentsBuilder

                            .fromUriString(
                                    wooCommerceUrl
                            )

                            .path(
                                    "/checkout/order-pay/"
                            )

                            .path(
                                    order.getId().toString()
                            )

                            .path("/")

                            .queryParam(
                                    "pay_for_order",
                                    "true"
                            )

                            .queryParam(
                                    "key",
                                    order.getOrderKey()
                            )

                            .build()

                            .toUriString();


            return "redirect:" + paymentUrl;
        }


        /*
         * CASH ON DELIVERY
         */

        session.removeAttribute("cart");


        model.addAttribute(
                "order",
                order
        );


        return "order-confirmation";
    }
}