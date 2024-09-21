package com.shopme.controller;

import com.nimbusds.jose.JOSEException;
import com.shopme.dto.request.IntrospectRequest;
import com.shopme.dto.request.LoginRequest;
import com.shopme.dto.request.LogoutRequest;
import com.shopme.dto.request.RefreshRequest;
import com.shopme.dto.response.ApiResponse;
import com.shopme.dto.response.AuthenticationResponse;
import com.shopme.entity.InvalidatedToken;
import com.shopme.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticateService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest loginRequest) throws Exception {

        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        apiResponse.setStatus("SUCCESS");

        AuthenticationResponse response = authenticateService.authenticate(loginRequest);
        apiResponse.setData(response);

        return apiResponse;
    }

    @PostMapping("/introspect")
    public ApiResponse<?> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {

        var result = authenticateService.introspect(request);
        return ApiResponse.builder()
                .status("SUCCESS")
               .data(result)
               .build();
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {

        authenticateService.logout(request);

        return ApiResponse.builder()
                .status("SUCCESS")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(@RequestBody RefreshRequest request) throws ParseException, JOSEException {

        var result = authenticateService.refreshToken(request);

        return ApiResponse.builder()
                .status("SUCCESS")
                .data(result)
                .build();
    }

}
