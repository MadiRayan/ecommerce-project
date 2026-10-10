package com.example.store.controller;

import java.util.List;

import com.example.store.entity.OrderEntity;
import com.example.store.repository.OrderRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

    private final OrderRepository
            orderRepository;


    public OrderController(
            OrderRepository orderRepository) {

        this.orderRepository =
                orderRepository;
    }


    @GetMapping("/orders")
    public String orders(
            Model model) {


        List<OrderEntity> orders =
                orderRepository
                        .findAllByOrderByWooCommerceOrderIdDesc();


        model.addAttribute(
                "orders",
                orders
        );


        return "orders";
    }
}