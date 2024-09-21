package com.shopme.mapper;

import com.shopme.dto.request.UserRequest;
import com.shopme.dto.response.UserResponse;
import com.shopme.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(user.buildRole())")
    @Mapping(target = "avatarPath", expression = "java(user.getAvatarPath())")
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    User toUser(UserRequest  userRequest);
}
