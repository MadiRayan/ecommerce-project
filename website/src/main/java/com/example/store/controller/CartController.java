package com.example.store.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.store.model.CartItem;
import com.example.store.model.Product;
import com.example.store.service.WooCommerceService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    private final WooCommerceService wooCommerceService;


    public CartController(
            WooCommerceService wooCommerceService) {

        this.wooCommerceService =
                wooCommerceService;
    }


    @SuppressWarnings("unchecked")
    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Long productId,
            HttpSession session) {

        List<CartItem> cart =
                (List<CartItem>)
                session.getAttribute("cart");


        if (cart == null) {

            cart = new ArrayList<>();
        }


        boolean alreadyExists = false;


        for (CartItem item : cart) {

            if (item.getProduct()
                    .getId()
                    .equals(productId)) {

                item.setQuantity(
                        item.getQuantity() + 1
                );

                alreadyExists = true;

                break;
            }
        }


        if (!alreadyExists) {

            Product product =
                    wooCommerceService
                            .getProduct(productId);

            cart.add(
                    new CartItem(product, 1)
            );
        }


        session.setAttribute(
                "cart",
                cart
        );


        return "redirect:/cart";
    }


    @SuppressWarnings("unchecked")
    @PostMapping("/cart/increase")
    public String increaseQuantity(
            @RequestParam Long productId,
            HttpSession session) {

        List<CartItem> cart =
                (List<CartItem>)
                session.getAttribute("cart");


        if (cart != null) {

            for (CartItem item : cart) {

                if (item.getProduct()
                        .getId()
                        .equals(productId)) {

                    item.setQuantity(
                            item.getQuantity() + 1
                    );

                    break;
                }
            }
        }


        return "redirect:/cart";
    }


    @SuppressWarnings("unchecked")
    @PostMapping("/cart/decrease")
    public String decreaseQuantity(
            @RequestParam Long productId,
            HttpSession session) {

        List<CartItem> cart =
                (List<CartItem>)
                session.getAttribute("cart");


        if (cart != null) {

            CartItem itemToRemove = null;


            for (CartItem item : cart) {

                if (item.getProduct()
                        .getId()
                        .equals(productId)) {

                    if (item.getQuantity() > 1) {

                        item.setQuantity(
                                item.getQuantity() - 1
                        );

                    } else {

                        itemToRemove = item;
                    }

                    break;
                }
            }


            if (itemToRemove != null) {

                cart.remove(itemToRemove);
            }
        }


        return "redirect:/cart";
    }


    @SuppressWarnings("unchecked")
    @PostMapping("/cart/remove")
    public String removeItem(
            @RequestParam Long productId,
            HttpSession session) {

        List<CartItem> cart =
                (List<CartItem>)
                session.getAttribute("cart");


        if (cart != null) {

            cart.removeIf(
                    item ->
                        item.getProduct()
                            .getId()
                            .equals(productId)
            );
        }


        return "redirect:/cart";
    }


    @SuppressWarnings("unchecked")
    @GetMapping("/cart")
    public String viewCart(
            HttpSession session,
            Model model) {

        List<CartItem> cart =
                (List<CartItem>)
                session.getAttribute("cart");


        if (cart == null) {

            cart = new ArrayList<>();
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


        return "cart";
    }
}