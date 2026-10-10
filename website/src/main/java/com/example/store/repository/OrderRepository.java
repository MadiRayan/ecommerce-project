package com.example.store.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.store.entity.OrderEntity;

public interface OrderRepository
        extends JpaRepository<OrderEntity, Long> {


    Optional<OrderEntity>
            findByWooCommerceOrderId(
                    Long wooCommerceOrderId
            );


    List<OrderEntity>
            findAllByOrderByWooCommerceOrderIdDesc();


    /*
     * GET ORDERS FOR ONE CUSTOMER
     * USING THEIR EMAIL ADDRESS
     */

    List<OrderEntity>
            findAllByEmailIgnoreCaseOrderByWooCommerceOrderIdDesc(
                    String email
            );
}