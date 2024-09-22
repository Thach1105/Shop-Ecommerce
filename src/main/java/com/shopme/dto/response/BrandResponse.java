package com.shopme.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class BrandResponse {

    Integer id;
    String name;
    String logo;

    List<String> categories;
}
