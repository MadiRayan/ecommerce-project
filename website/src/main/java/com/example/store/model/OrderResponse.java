package com.example.store.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {

    private Long id;

    private String number;

    private String status;

    private String currency;

    private String total;

    @JsonProperty("order_key")
    private String orderKey;


    public OrderResponse() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getNumber() {
        return number;
    }


    public void setNumber(String number) {
        this.number = number;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public String getTotal() {
        return total;
    }


    public void setTotal(String total) {
        this.total = total;
    }


    public String getOrderKey() {
        return orderKey;
    }


    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }
}