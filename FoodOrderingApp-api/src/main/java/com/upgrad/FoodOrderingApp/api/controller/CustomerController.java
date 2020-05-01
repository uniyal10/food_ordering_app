package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    @Autowired
    private SignupBusinessService customerService;

    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUp(@RequestBody final SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException {
        if (signupCustomerRequest.getLastName().isEmpty()) {
            throw new SignUpRestrictedException("SGR -005", "Last name cannot be empty");
        }

        signupCustomerRequest.getContactNumber().length();

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmailAddress(signupCustomerRequest.getEmailAddress());
        customerEntity.setLastname(signupCustomerRequest.getLastName());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setSalt("salt");
        customerEntity.setUuid(UUID.randomUUID().toString());

        final CustomerEntity responseCustomer = customerService.save(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse();
        signupCustomerResponse.setId(responseCustomer.getUuid());
        signupCustomerResponse.setStatus("Customer Registered");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);
    }
  
}