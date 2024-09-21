package com.shopme.util;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageInfo {
    int size;
    long totalElements;
    int totalPages;
    int number;
}
