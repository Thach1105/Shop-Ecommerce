package com.shopme.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {


    Integer id;
    String username;
    String firstName;
    String lastName;
    String email;
    String avatar;
    boolean enabled;

    String roles;

    String avatarPath;
}
