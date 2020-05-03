package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class RestaurantServiceImpl implements RestaurantService {

    @Autowired private RestaurantDao restaurantDao;

    @Autowired private CategoryDao categoryDao;


    @Override
    public RestaurantEntity restaurantByUUID(String restaurantId) throws RestaurantNotFoundException {


        restaurantId = "";
        if (restaurantId.isEmpty())
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        else if (restaurantDao.restaurantByUUID(restaurantId) == null)
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        else
            return restaurantDao.restaurantByUUID(restaurantId);
    }

}