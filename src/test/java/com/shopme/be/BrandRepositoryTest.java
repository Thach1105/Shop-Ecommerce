package com.shopme.be;


import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import com.shopme.repository.BrandRepository;
import com.shopme.repository.CategoryRepository;
import jdk.swing.interop.SwingInterOpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void testCreateBrand(){
        List<Integer> listCategories = List.of(1, 4);

        Brand samsung = Brand.builder()
                .name("SAM SUNG")
                .logo("samsung.png")
                .categories(new HashSet<>(categoryRepository.findAllById(listCategories)))
                .build();

        brandRepository.save(samsung);
    }

    @Test
    public void testGetBrand(){
        Brand brand = brandRepository.findById(1)
                .orElseThrow();

        System.out.println(brand);
    }
}
