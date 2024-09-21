package com.shopme.admin.user;


import com.shopme.entity.Role;
import com.shopme.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole(){

        Role roleAdmin = Role.builder()
                .name("ADMIN")
                .description("manage everything")
                .build();

        Role saveRole = roleRepository.save(roleAdmin);
        assert (saveRole.getName().equals("ADMIN"));
    }

    @Test
    public void testCreatRestRole(){

        Role roleSeller = Role.builder()
                .name("SELLER")
                .description("manage product price, customer, shipping, "
                        + "orders and sales report")
                .build();

        Role roleEditor = Role.builder()
                .name("EDITOR")
                .description("manage categories, brands, "
                    + "products, articles and menus")
                .build();

        Role roleShipper = Role.builder()
                .name("SHIPPER")
                .description("view products, view orders, "
                + "and update order status")
                .build();

        Role roleAssistant = Role.builder()
                .name("ASSISTANT")
                .description("manage questions and reviews")
                .build();

        roleRepository.saveAll(List.of(roleEditor, roleSeller, roleShipper, roleAssistant));
    }
}
