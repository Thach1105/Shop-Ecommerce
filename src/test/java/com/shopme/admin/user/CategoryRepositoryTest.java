package com.shopme.admin.user;

import com.shopme.dto.response.CategoryResponse;
import com.shopme.entity.Category;
import com.shopme.mapper.CategoryMapper;
import com.shopme.repository.CategoryRepository;
import com.shopme.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

   /* @Autowired
    private CategoryMapper categoryMapper;*/

    @Test
    public void testCreateRootCategory() {
        Category phone = Category.builder()
                .name("Điện thoại")
                .alias("phone")
                .build();
        categoryRepository.save(phone);

        Category accessory = Category.builder()
                .name("Phụ kiện")
                .alias("accessory")
                .build();
        categoryRepository.save(accessory);

        /*Category hub = new Category("Hub, Cáp chuyển đổi", accessory);
        categoryRepository.save(hub);

        Category sacDuPhong = new Category("Sạc dự phòng", accessory);
        categoryRepository.save(sacDuPhong);*/
    }

    @Test
    public void testGetCategory(){
        Category category = categoryRepository.findById(2).orElseThrow();
        System.out.println(category);

        Set<Category> children = category.getChildren();
        for (var cate : children){
            System.out.println(cate);
        }
    }

    @Test
    public void testGetRootCategory(){
        int pageNumber = 0;
        int pageSize = 4;
        Sort sort = Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        List<Category> listRoot = categoryRepository.findRootCategories(pageable).getContent();

        for (var c : listRoot){
            System.out.println(c);
        }

    }

}
