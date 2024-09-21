package com.shopme.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "upload-avatar";
        Path userAvatarDir = Paths.get(dirName);

        // lấy đường dẫn tuyệt đối đến thư mục upload-avatar
        String userAvatarPath = userAvatarDir.toFile().getAbsolutePath();

        // tạo ánh xạ URL đến thư mục thực tế trên hệ thống
        registry.addResourceHandler("/" + dirName + "/**")
                //chỉ định vị trí tài nguyên thực tế
                .addResourceLocations("file:/" + userAvatarPath + "/");


        registry.addResourceHandler("/api/images/**")
                .addResourceLocations("file:/static/images/");
    }

}
