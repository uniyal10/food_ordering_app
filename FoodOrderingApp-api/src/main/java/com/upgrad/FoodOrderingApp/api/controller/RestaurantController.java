package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.upgrad.FoodOrderingApp.api.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(method = RequestMethod.GET, path = "/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantDetails(@PathVariable("restaurant_id") String restaurantId) throws
            RestaurantNotFoundException {

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        //It might be possible that getCategories will return empty list
        List<CategoryEntity> restaurantCategoryEntityList = restaurantEntity.getCategories();

        //



        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState().id(
                UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).stateName(restaurantEntity.getAddress().getState().getStateName());
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress().id(UUID.fromString(restaurantEntity.getAddress().getUuid())).flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).city(restaurantEntity.getAddress().getCity()).
                locality(restaurantEntity.getAddress().getLocality()).pincode(restaurantEntity.getAddress().getPincode()).state(restaurantDetailsResponseAddressState);
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse().id(UUID.fromString(restaurantEntity.getUuid())).restaurantName(restaurantEntity.getRestaurantName()).
                averagePrice(restaurantEntity.getAvgPrice()).customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating())).numberCustomersRated(restaurantEntity.getNumberCustomersRated()).
                photoURL(restaurantEntity.getPhotoUrl()).address(restaurantDetailsResponseAddress);




        for (CategoryEntity categoryEntity : restaurantCategoryEntityList) {

            CategoryList categoryList = new CategoryList().id(UUID.fromString(categoryEntity.getUuid())).categoryName(categoryEntity.getCategoryName());

            List<ItemEntity> categoryItemEntities = itemService.getItemsByCategoryAndRestaurant(restaurantId, categoryEntity.getUuid());

            for (ItemEntity itemEntity : categoryItemEntities) {

                ItemList itemList = new ItemList().id(UUID.fromString(itemEntity.getUuid())).itemName(itemEntity.getItemName()).itemType(ItemList.ItemTypeEnum.valueOf(itemEntity.getType().name())).
                        price(itemEntity.getPrice());
                categoryList.addItemListItem(itemList);
            }
            restaurantDetailsResponse.addCategoriesItem(categoryList);
        }
        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }
}