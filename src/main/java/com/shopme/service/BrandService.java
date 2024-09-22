package com.shopme.service;

import com.shopme.dto.request.BrandRequest;
import com.shopme.dto.response.BrandResponse;
import com.shopme.entity.Brand;
import com.shopme.entity.Category;
import com.shopme.exception.AppException;
import com.shopme.exception.ErrorCode;
import com.shopme.mapper.BrandMapper;
import com.shopme.repository.BrandRepository;
import com.shopme.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandMapper brandMapper;

    public static final String BRAND_PAGE_SIZE = "10";

    public Brand create(BrandRequest request){
        if(brandRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.BRAND_EXISTED);

        Brand newBrand =  brandMapper.toBrand(request);
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategories()));
        newBrand.setCategories(categories);

        return brandRepository.save(newBrand);
    }

    public List<Brand> getAll(){
        return brandRepository.findAll();
    }

    public Page<Brand> getByPage(Integer pageNum, Integer pageSize, String sortField, String keyword){
        Sort sort = sortField != null ? Sort.by(sortField) : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNum-1, pageSize, sort);

        if(keyword != null) {
            return brandRepository.findBrandByKeyWord(keyword, pageable);
        } else {
            return brandRepository.findAll(pageable);
        }
    }

    public Brand getById(Integer id){
        return brandRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.BRAND_ID_INVALID)
        );

    }

    public Brand update(Integer id, BrandRequest request){
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.BRAND_ID_INVALID)
        );

        if(!brand.getName().equalsIgnoreCase(request.getName())
                && brandRepository.existsByName(request.getName())
        ) throw new AppException(ErrorCode.BRAND_EXISTED);

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategories()));
        brand.setName(request.getName());
        brand.setCategories(categories);
        if(request.getLogo() != null){
            brand.setLogo(request.getLogo());
        }

        return brandRepository.save(brand);
    }

    public boolean delete(Integer id){
        if(!brandRepository.existsById(id)) {
            return false;
        }else {
            brandRepository.deleteById(id);
            return true;
        }
    }
}
