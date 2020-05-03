package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface CategoryService {


    List<CategoryEntity> getAllCategories();



}
