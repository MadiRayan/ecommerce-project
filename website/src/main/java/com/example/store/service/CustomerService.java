package com.example.store.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.store.entity.CustomerEntity;
import com.example.store.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    public CustomerService(
            CustomerRepository customerRepository) {

        this.customerRepository =
                customerRepository;

        this.passwordEncoder =
                new BCryptPasswordEncoder();
    }


    public CustomerEntity register(
            String firstName,
            String lastName,
            String email,
            String password) {


        String cleanEmail =
                email.trim().toLowerCase();


        if (
                customerRepository
                        .existsByEmailIgnoreCase(cleanEmail)
        ) {

            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }


        CustomerEntity customer =
                new CustomerEntity();


        customer.setFirstName(
                firstName.trim()
        );

        customer.setLastName(
                lastName.trim()
        );

        customer.setEmail(
                cleanEmail
        );

        customer.setPasswordHash(
                passwordEncoder.encode(password)
        );

        customer.setCreatedAt(
                LocalDateTime.now()
        );


        return customerRepository.save(customer);
    }


    public CustomerEntity login(
            String email,
            String password) {


        Optional<CustomerEntity> optionalCustomer =
                customerRepository
                        .findByEmailIgnoreCase(
                                email.trim()
                        );


        if (optionalCustomer.isEmpty()) {

            return null;
        }


        CustomerEntity customer =
                optionalCustomer.get();


        if (
                !passwordEncoder.matches(
                        password,
                        customer.getPasswordHash()
                )
        ) {

            return null;
        }


        return customer;
    }


    public CustomerEntity findById(Long id) {

        if (id == null) {

            return null;
        }


        return customerRepository
                .findById(id)
                .orElse(null);
    }
}