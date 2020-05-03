package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class CategoryController {


    @Autowired
    CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/Category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<CategoryListResponse>> getAllAddresses() {
//        String access_token = authorization.split("Bearer ")[1];
//        CustomerEntity customerEntity = customerService.getCustomer(access_token);
        List<CategoryEntity> categoryEntities = categoryService.getAllCategories();
        List<CategoryListResponse> list = new ArrayList<CategoryListResponse>();
        
        for(CategoryEntity categoryEntity : categoryEntities){
            CategoryListResponse categoryListResponse = new CategoryListResponse();
            categoryListResponse.setId(UUID.fromString(categoryEntity.getUuid()));
            categoryListResponse.setCategoryName(categoryEntity.getCategoryName());
            list.add(categoryListResponse);
        }
        return new  ResponseEntity<List<CategoryListResponse>>(list,HttpStatus.OK);
    }



}
