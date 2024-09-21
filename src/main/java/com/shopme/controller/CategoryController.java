package com.shopme.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.dto.request.CategoryRequest;
import com.shopme.dto.response.ApiResponse;
import com.shopme.dto.response.CategoryResponse;
import com.shopme.entity.Category;
import com.shopme.export.CategoryExporter.CategoryExcelExporter;
import com.shopme.mapper.CategoryMapper;
import com.shopme.service.CategoryService;
import com.shopme.util.FileUploadUtil;
import com.shopme.util.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategory(){
        var list = categoryService.getAll();
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status("SUCCESS")
                .data(list)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }


    @GetMapping
    public ResponseEntity<?> getCategories(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", defaultValue = CategoryService.CATEGORY_PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "keyword", required = false) String keyword
    ){

        Page<Category> page = categoryService.getRootCategories(pageNum-1, pageSize, sortField, keyword);

        PageInfo pageInfo = PageInfo.builder()
                .number(page.getNumber() + 1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();

        List<CategoryResponse> list = page.getContent().stream().map(categoryMapper::toCategoryResponse).toList();

        return ResponseEntity
                .ok()
                .body(
                        ApiResponse.builder()
                                .status("SUCCESS")
                                .data(list)
                                .pageDetails(pageInfo)
                                .build()
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Integer id){
        var category = categoryService.getById(id);
        return ResponseEntity
                .ok()
                .body(ApiResponse.builder()
                        .status("SUCCESS")
                        .data(category)
                        .build());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestPart(name = "category") String  categoryJSON,
                                                 @RequestPart("image") MultipartFile image)
            throws IOException {

        // chuyển đối JSON thành đối tượng CategoryRequest
        ObjectMapper objectMapper = new ObjectMapper();
        CategoryRequest categoryRequest;

        try {
            categoryRequest = objectMapper.readValue(categoryJSON, CategoryRequest.class);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid Category JSON");
        }

        // gán tên ảnh vào trong đôi tượng CategoryRequest
        String imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        categoryRequest.setImage(imageName);

        // lưu danh mục mới vào cơ sở dữ liệu
        var newCategory = categoryService.save(categoryRequest);

        // lưu ảnh danh mục
        String uploadDir = "category-images/" + newCategory.getId();
        FileUploadUtil.saveFile(uploadDir, imageName, image);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .status("SUCCESS")
                        .data(newCategory)
                        .build());
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Integer id,
                                            @RequestPart(name = "category")String categoryJSON,
                                            @RequestPart("image") MultipartFile image) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CategoryRequest categoryRequest;
        try {
            categoryRequest = objectMapper.readValue(categoryJSON, CategoryRequest.class);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Invalid Category JSON");
        }

        if(!image.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            categoryRequest.setImage(fileName);
            String uploadDir = "category-images/" + id;
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }

        var postCategory = categoryService.update(id, categoryRequest);

        return ResponseEntity
                .ok()
                .body(ApiResponse.builder()
                        .status("SUCCESS")
                        .data(postCategory)
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) throws IOException {

        categoryService.delete(id);
        return ResponseEntity
                .ok()
                .body(
                        ApiResponse.builder()
                                .status("SUCCESS")
                                .data("Deleted Category completed")
                                .build()
                );
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        var list = categoryService.getAll();
        CategoryExcelExporter exporter = new CategoryExcelExporter();
        exporter.export(list, response);
    }
}
