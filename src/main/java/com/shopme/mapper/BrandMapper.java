package com.shopme.mapper;


import com.shopme.dto.request.BrandRequest;
import com.shopme.dto.response.BrandResponse;
import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "categories", ignore = true)
    Brand toBrand(BrandRequest request);

    @Mapping(target = "categories", expression = "java(buildCategories(brand))")
    BrandResponse toBrandResponse(Brand brand);

    default List<String> buildCategories(Brand brand){
        if(brand.getCategories() != null){
            Set<Category> categories = brand.getCategories();
            return categories.stream().map(Category::getName).toList();
        } else return null;

    }
}
