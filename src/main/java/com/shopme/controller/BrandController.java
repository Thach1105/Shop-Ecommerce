package com.shopme.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.dto.request.BrandRequest;
import com.shopme.dto.response.ApiResponse;
import com.shopme.dto.response.BrandResponse;
import com.shopme.entity.Brand;
import com.shopme.export.BrandExporter.BrandExcelExporter;
import com.shopme.mapper.BrandMapper;
import com.shopme.service.BrandService;
import com.shopme.service.CategoryService;
import com.shopme.util.FileUploadUtil;
import com.shopme.util.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandMapper brandMapper;

    @PostMapping
    public ResponseEntity<?> createBrand(
            @RequestPart("brand") String brandJSON,
            @RequestPart("logo") MultipartFile file
            ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        BrandRequest brandRequest;
        try {
            brandRequest = objectMapper.readValue(brandJSON, BrandRequest.class);
        } catch (IOException e){
            return ResponseEntity.badRequest().body("Invalid Brand JSON");
        }

        String logoName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        brandRequest.setLogo(logoName);

        BrandResponse newBrand = brandMapper.toBrandResponse(brandService.create(brandRequest));
        String uploadDir = "brand-logo/" + newBrand.getId();
        FileUploadUtil.saveFile(uploadDir,logoName, file);

        return ResponseEntity.ok()
                .body(ApiResponse.builder()
                        .status("SUCCESS")
                        .data(newBrand)
                        .build());
    }

    @GetMapping
    public ResponseEntity<?> getBrands(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "size", defaultValue = BrandService.BRAND_PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "keyword", required = false) String keyword
    ){
        Page<Brand> pages = brandService.getByPage(pageNum, pageSize, sortField, keyword);

        PageInfo pageInfo = PageInfo.builder()
                .totalPages(pages.getTotalPages())
                .totalElements(pages.getTotalElements())
                .size(pages.getSize())
                .number(pages.getNumber()+1)
                .build();

        List<BrandResponse> brands = pages.getContent().stream()
                .map(brandMapper::toBrandResponse).toList();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status("SUCCESS")
                .data(brands)
                .pageDetails(pageInfo)
                .build();

       return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable("id")Integer id){
        Brand brand = brandService.getById(id);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status("SUCCESS")
                .data(brandMapper.toBrandResponse(brand))
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id")Integer id,
            @RequestPart("brand") String brandJSON,
            @RequestPart(name = "logo", required = false) MultipartFile file
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        BrandRequest brandRequest;
        try {
            brandRequest = objectMapper.readValue(brandJSON, BrandRequest.class);
        } catch (IOException e){
            return ResponseEntity.badRequest().body("Invalid Brand JSON");
        }

        if(file != null){
            String logoName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            brandRequest.setLogo(logoName);
            String uploadDir = "brand-logo/" + id;
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, logoName, file);
        }

        Brand postBrand = brandService.update(id, brandRequest);

        return ResponseEntity.ok()
                .body(
                        ApiResponse.builder()
                                .status("SUCCESS")
                                .data(brandMapper.toBrandResponse(postBrand))
                                .build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id){
        boolean deleted = brandService.delete(id);
        if(deleted){
            return ResponseEntity.ok()
                    .body(
                            ApiResponse.builder()
                                    .status("SUCCESS")
                                    .data("Successfully deleted")
                                    .build()
                    );
        } else {
            return ResponseEntity.badRequest()
                    .body(
                            ApiResponse.builder()
                                    .status("ERROR")
                                    .message(String.format("Could not found brand with id: %d", id))
                                    .build()
                    );
        }
    }

    @GetMapping("/export-excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<BrandResponse> list = brandService.getAll().stream()
                .map(brandMapper::toBrandResponse).toList();

        BrandExcelExporter exporter = new BrandExcelExporter();
        exporter.export(list, response);
    }
}
