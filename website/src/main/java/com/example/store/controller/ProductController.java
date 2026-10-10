package com.example.store.controller;

import com.example.store.model.Product;
import com.example.store.service.WooCommerceService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    private final WooCommerceService wooCommerceService;

    public ProductController(
            WooCommerceService wooCommerceService) {

        this.wooCommerceService =
                wooCommerceService;
    }

    @GetMapping("/products")
    public String products(Model model) {

        Product[] products =
                wooCommerceService.getProducts();

        model.addAttribute(
                "products",
                products
        );

        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(
            @PathVariable Long id,
            Model model) {

        Product product =
                wooCommerceService.getProduct(id);

        model.addAttribute(
                "product",
                product
        );

        return "product-details";
    }
}