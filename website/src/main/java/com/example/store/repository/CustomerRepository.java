package com.example.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.store.entity.CustomerEntity;

public interface CustomerRepository
        extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity>
            findByEmailIgnoreCase(String email);

    boolean
            existsByEmailIgnoreCase(String email);
}