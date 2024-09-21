package com.shopme.controller;

import com.shopme.dto.response.ApiResponse;
import com.shopme.entity.Role;
import com.shopme.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping
    public ApiResponse<List<Role>> getRoles(){
        List<Role> roles =  roleService.getRoles();

        return ApiResponse.<List<Role>>builder()
                .status("SUCCESS")
                .data(roles)
                .build();
    }

}
