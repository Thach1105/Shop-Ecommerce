package com.shopme.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;


import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class UserRequest {

    @Size(min = 5, message = "Username must be at least 5 characters")
    String username;

    String password;

    @NotEmpty
    String firstName;

    @NotEmpty
    String lastName;

    @Email@NotEmpty
    String email;

    String avatar;
    boolean enabled;

    List<String> roles;
}
