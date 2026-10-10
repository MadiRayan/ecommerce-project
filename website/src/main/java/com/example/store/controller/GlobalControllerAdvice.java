package com.example.store.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {


    @ModelAttribute("loggedIn")
    public boolean loggedIn(
            HttpSession session) {

        return session.getAttribute(
                "customerId"
        ) != null;
    }


    @ModelAttribute("customerName")
    public String customerName(
            HttpSession session) {

        Object name =
                session.getAttribute(
                        "customerName"
                );

        if (name == null) {

            return null;
        }

        return name.toString();
    }
}