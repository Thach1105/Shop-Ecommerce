package com.shopme.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopme.util.PageInfo;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ApiResponse<T> {

    String status;
    String message;
    T data;
    PageInfo pageDetails;
}
