package com.example.store.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_orders")
public class OrderEntity {


    /*
     * LOCAL DATABASE ID
     */

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    /*
     * WOOCOMMERCE ORDER ID
     */

    @Column(
            unique = true,
            nullable = false
    )
    private Long wooCommerceOrderId;


    private String orderNumber;


    private String status;


    private String customerFirstName;


    private String customerLastName;


    private String email;


    private BigDecimal total;


    private String currency;


    private String paymentMethod;


    private LocalDateTime lastUpdated;


    public OrderEntity() {
    }


    public Long getId() {

        return id;
    }


    public void setId(Long id) {

        this.id = id;
    }


    public Long getWooCommerceOrderId() {

        return wooCommerceOrderId;
    }


    public void setWooCommerceOrderId(
            Long wooCommerceOrderId) {

        this.wooCommerceOrderId =
                wooCommerceOrderId;
    }


    public String getOrderNumber() {

        return orderNumber;
    }


    public void setOrderNumber(
            String orderNumber) {

        this.orderNumber =
                orderNumber;
    }


    public String getStatus() {

        return status;
    }


    public void setStatus(
            String status) {

        this.status =
                status;
    }


    public String getCustomerFirstName() {

        return customerFirstName;
    }


    public void setCustomerFirstName(
            String customerFirstName) {

        this.customerFirstName =
                customerFirstName;
    }


    public String getCustomerLastName() {

        return customerLastName;
    }


    public void setCustomerLastName(
            String customerLastName) {

        this.customerLastName =
                customerLastName;
    }


    public String getEmail() {

        return email;
    }


    public void setEmail(
            String email) {

        this.email =
                email;
    }


    public BigDecimal getTotal() {

        return total;
    }


    public void setTotal(
            BigDecimal total) {

        this.total =
                total;
    }


    public String getCurrency() {

        return currency;
    }


    public void setCurrency(
            String currency) {

        this.currency =
                currency;
    }


    public String getPaymentMethod() {

        return paymentMethod;
    }


    public void setPaymentMethod(
            String paymentMethod) {

        this.paymentMethod =
                paymentMethod;
    }


    public LocalDateTime getLastUpdated() {

        return lastUpdated;
    }


    public void setLastUpdated(
            LocalDateTime lastUpdated) {

        this.lastUpdated =
                lastUpdated;
    }


    public String getCustomerName() {

        String first =
                customerFirstName == null
                        ? ""
                        : customerFirstName;


        String last =
                customerLastName == null
                        ? ""
                        : customerLastName;


        return (
                first
                + " "
                + last
        ).trim();
    }
}
