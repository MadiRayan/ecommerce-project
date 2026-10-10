<?php

/**
 * Plugin Name: Spring Boot Payment Return
 * Description: Redirects completed Stripe orders back to the Spring Boot store.
 * Version: 2.0
 */

if (!defined('ABSPATH')) {
    exit;
}


/*
 * SPRING BOOT SUCCESS PAGE
 */

function spring_boot_success_url() {

    return 'https://trustless-stingily-twisty.ngrok-free.dev/payment/success';
}


/*
 * ALLOW THE NGROK HOST FOR SAFE REDIRECTS
 */

add_filter(
    'allowed_redirect_hosts',
    'spring_boot_allow_ngrok_host'
);

function spring_boot_allow_ngrok_host($hosts) {

    $host = wp_parse_url(
        spring_boot_success_url(),
        PHP_URL_HOST
    );

    if (
        $host &&
        !in_array($host, $hosts, true)
    ) {

        $hosts[] = $host;
    }

    return $hosts;
}


/*
 * REDIRECT ONLY AFTER WOOCOMMERCE
 * HAS FINISHED THE ORDER
 */

add_action(
    'template_redirect',
    'spring_boot_redirect_paid_stripe_order'
);

function spring_boot_redirect_paid_stripe_order() {


    /*
     * ONLY RUN ON THE
     * WOOCOMMERCE ORDER RECEIVED PAGE
     */

    if (
        !function_exists('is_wc_endpoint_url') ||
        !is_wc_endpoint_url('order-received')
    ) {

        return;
    }


    /*
     * GET ORDER ID
     */

    $order_id =
        absint(
            get_query_var('order-received')
        );


    if (!$order_id) {

        return;
    }


    /*
     * GET ORDER
     */

    $order =
        wc_get_order($order_id);


    if (!$order) {

        return;
    }


    /*
     * ONLY STRIPE ORDERS
     */

    if (
        $order->get_payment_method()
        !==
        'stripe'
    ) {

        return;
    }


    /*
     * VERIFY ORDER KEY
     */

    $order_key = '';

    if (isset($_GET['key'])) {

        $order_key =
            wc_clean(
                wp_unslash(
                    $_GET['key']
                )
            );
    }


    if (
        empty($order_key) ||
        !$order->key_is_valid($order_key)
    ) {

        return;
    }


    /*
     * VERIFY PAYMENT
     */

    $status =
        $order->get_status();


    $payment_complete =

        $order->is_paid()

        ||

        in_array(
            $status,
            array(
                'processing',
                'completed'
            ),
            true
        );


    if (!$payment_complete) {

        return;
    }


    /*
     * BUILD SPRING BOOT URL
     */

    $redirect_url =
        add_query_arg(
            'orderId',
            $order_id,
            spring_boot_success_url()
        );


    /*
     * REDIRECT TO SPRING BOOT
     */

    wp_safe_redirect(
        $redirect_url
    );

    exit;
}