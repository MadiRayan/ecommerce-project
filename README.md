# WooCommerce E-Commerce Project

This repository contains both a web application and a mobile application integrated with WooCommerce.

## Website

The `website` folder contains a Spring Boot e-commerce website.

Technologies:
- Java
- Spring Boot
- Thymeleaf
- CSS
- WooCommerce
- H2 Database
- Stripe test payments
- WooCommerce webhooks

Main features:
- Product catalog
- Product details
- Shopping cart
- Customer registration and login
- Checkout
- Cash on Delivery
- Stripe test payments
- WooCommerce order creation
- Order history
- Webhook synchronization

## Mobile Application

The `mobile-app` folder contains a Flutter mobile application.

Technologies:
- Flutter
- Dart
- WooCommerce REST API

Main features:
- WooCommerce product catalog
- Product categories
- Shopping cart
- Checkout
- Order creation
- Product creation
- Direct WooCommerce integration

## WooCommerce

Both applications connect to WooCommerce.

The Spring Boot website and Flutter application are separate applications but can use the same WooCommerce store.

## Configuration

Real credentials are intentionally excluded from this repository.

### Website

Copy:

website/src/main/resources/application-example.properties

to:

website/src/main/resources/application.properties

Then provide the required WooCommerce credentials.

### Mobile Application

Copy:

mobile-app/lib/config/woo_config.example.dart

to:

mobile-app/lib/config/woo_config.dart

Then provide the WooCommerce URL and mobile API credentials.

## Security

WooCommerce API keys, webhook secrets, local databases, and generated build files are excluded from Git.