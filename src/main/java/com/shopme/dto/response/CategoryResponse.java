package com.shopme.dto.response;

import com.shopme.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    Integer id;
    String name;
    String alias;
    String description;
    String image;
    boolean enabled;

    List<CategoryResponse> children;
}
