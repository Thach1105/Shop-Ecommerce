package com.shopme.admin.user;

import com.shopme.entity.Role;
import com.shopme.entity.User;
import com.shopme.repository.RoleRepository;
import com.shopme.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;


import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void testFirstCreateUser(){

        if(userRepository.existsByUsername("admin")) return;

        var adminRole = roleRepository.findById("ADMIN").orElseThrow();

        User user1 = User.builder()
                .username("admin")
                .password("admin")
                .firstName("Thach")
                .lastName("Nguyen Ngoc")
                .email("thach11052002@gmail.com")
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(user1);
    }


    @Test
    public void testGetAllUser(){
        var users = userRepository.findAll();

        users.forEach(
                System.out::println
        );
    }

    @Test
    public void testGetByUsername(){
        String username = "vi";
        User user = userRepository.findByUsername(username);

        System.out.println(user);
    }

    @Test
    public void testUpdateUser(){
        User user = userRepository.findById(5).orElseThrow(() ->  new RuntimeException("User not found"));
        user.setEnabled(true);

        System.out.println(user);
        userRepository.save(user);
    }


    @Test
    public void testDeleteUser(){
        userRepository.deleteById(2);
    }

    @Test
    public void testDuplicateEmail(){
        System.out.println(userRepository.existsByEmail("thach11052002@gmail.com"));
    }

    @Test
    public void testDisabledUser(){
        Integer id = 1;
        userRepository.updateEnabledStatus(1, false);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 8;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);

        System.out.println(page.getTotalPages());
        System.out.println(page.getTotalElements());
        List<User> userList = page.getContent();
        userList.forEach(
                System.out::println
        );

    }



}
