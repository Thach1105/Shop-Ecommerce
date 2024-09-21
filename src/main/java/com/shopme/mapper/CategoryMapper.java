package com.shopme.mapper;

import com.shopme.dto.request.CategoryRequest;
import com.shopme.dto.response.CategoryResponse;
import com.shopme.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryRequest request);

    @Mapping(target = "children", expression = "java(buildChildren(category))")
    CategoryResponse toCategoryResponse(Category category);

    default List<CategoryResponse> buildChildren(Category category) {
        return category.getChildren() != null
                ? category.getChildren().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList())
                : null;
    }
}
