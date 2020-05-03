package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

public interface CustomerService {

    CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException;
    CustomerAuthEntity login(String contactNumber, String password) throws AuthenticationFailedException;
    CustomerEntity getCustomer(String access_token) throws AuthorizationFailedException;
    void logout(String accessToken) throws AuthorizationFailedException;
}