package com.example.store.controller;

import java.util.List;

import com.example.store.entity.CustomerEntity;
import com.example.store.entity.OrderEntity;
import com.example.store.repository.OrderRepository;
import com.example.store.service.CustomerService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    private final CustomerService customerService;

    private final OrderRepository orderRepository;


    public AccountController(
            CustomerService customerService,
            OrderRepository orderRepository) {

        this.customerService =
                customerService;

        this.orderRepository =
                orderRepository;
    }


    /*
     * REGISTER PAGE
     */

    @GetMapping("/register")
    public String registerPage(
            HttpSession session) {


        if (
                session.getAttribute(
                        "customerId"
                ) != null
        ) {

            return "redirect:/account";
        }


        return "register";
    }


    /*
     * REGISTER CUSTOMER
     */

    @PostMapping("/register")
    public String register(

            @RequestParam
            String firstName,

            @RequestParam
            String lastName,

            @RequestParam
            String email,

            @RequestParam
            String password,

            @RequestParam
            String confirmPassword,

            HttpSession session,

            Model model) {


        if (
                password == null ||
                password.length() < 6
        ) {

            model.addAttribute(
                    "error",
                    "Password must contain at least 6 characters."
            );

            return "register";
        }


        if (
                !password.equals(
                        confirmPassword
                )
        ) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            return "register";
        }


        try {

            CustomerEntity customer =
                    customerService.register(
                            firstName,
                            lastName,
                            email,
                            password
                    );


            loginCustomer(
                    session,
                    customer
            );


            return "redirect:/account";


        } catch (
                IllegalArgumentException e
        ) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "register";
        }
    }


    /*
     * LOGIN PAGE
     */

    @GetMapping("/login")
    public String loginPage(
            HttpSession session) {


        if (
                session.getAttribute(
                        "customerId"
                ) != null
        ) {

            return "redirect:/account";
        }


        return "login";
    }


    /*
     * LOGIN CUSTOMER
     */

    @PostMapping("/login")
    public String login(

            @RequestParam
            String email,

            @RequestParam
            String password,

            HttpSession session,

            Model model) {


        CustomerEntity customer =
                customerService.login(
                        email,
                        password
                );


        if (customer == null) {

            model.addAttribute(
                    "error",
                    "Invalid email or password."
            );

            return "login";
        }


        loginCustomer(
                session,
                customer
        );


        return "redirect:/account";
    }


    /*
     * MY ACCOUNT
     */

    @GetMapping("/account")
    public String account(

            HttpSession session,

            Model model) {


        Long customerId =
                (Long)
                        session.getAttribute(
                                "customerId"
                        );


        if (customerId == null) {

            return "redirect:/login";
        }


        CustomerEntity customer =
                customerService.findById(
                        customerId
                );


        if (customer == null) {

            removeCustomerSession(
                    session
            );

            return "redirect:/login";
        }


        /*
         * GET ONLY THIS CUSTOMER'S ORDERS
         */

        List<OrderEntity> orders =
                orderRepository
                        .findAllByEmailIgnoreCaseOrderByWooCommerceOrderIdDesc(
                                customer.getEmail()
                        );


        model.addAttribute(
                "customer",
                customer
        );


        model.addAttribute(
                "orders",
                orders
        );


        return "account";
    }


    /*
     * LOGOUT
     */

    @PostMapping("/logout")
    public String logout(
            HttpSession session) {


        /*
         * ONLY REMOVE LOGIN INFORMATION.
         *
         * WE DO NOT INVALIDATE THE
         * WHOLE SESSION BECAUSE THAT
         * WOULD ALSO DELETE THE CART.
         */

        removeCustomerSession(
                session
        );


        return "redirect:/";
    }


    /*
     * LOGIN SESSION
     */

    private void loginCustomer(

            HttpSession session,

            CustomerEntity customer) {


        session.setAttribute(
                "customerId",
                customer.getId()
        );


        session.setAttribute(
                "customerName",
                customer.getFirstName()
        );


        session.setAttribute(
                "customerEmail",
                customer.getEmail()
        );
    }


    /*
     * REMOVE LOGIN SESSION
     */

    private void removeCustomerSession(
            HttpSession session) {


        session.removeAttribute(
                "customerId"
        );


        session.removeAttribute(
                "customerName"
        );


        session.removeAttribute(
                "customerEmail"
        );
    }
}