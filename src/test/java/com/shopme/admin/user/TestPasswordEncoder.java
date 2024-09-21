package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordEncoder {

    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "123456789";
        String encodePassword = passwordEncoder.encode(rawPassword);

        System.out.println(encodePassword);
        System.out.println(passwordEncoder.matches(rawPassword, encodePassword));
    }
}
