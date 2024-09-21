package com.shopme.service;

import com.shopme.dto.request.CategoryRequest;
import com.shopme.dto.response.CategoryResponse;
import com.shopme.entity.Category;
import com.shopme.exception.AppException;
import com.shopme.exception.ErrorCode;
import com.shopme.mapper.CategoryMapper;
import com.shopme.repository.CategoryRepository;
import com.shopme.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryMapper categoryMapper;

    public static final String CATEGORY_PAGE_SIZE = "5";

    public CategoryResponse save(CategoryRequest request){
        Category newCategory = categoryMapper.toCategory(request);
        Category parent;

        //kiểm tra xem có danh mục mẹ không nếu có thì gắn vào
        if(request.getParent() != null){
            int idParent = request.getParent();
            parent = categoryRepository.findById(idParent).orElseThrow();
            newCategory.setParent(parent);
        }

        return categoryMapper.toCategoryResponse(categoryRepository.save(newCategory));
    }


    public List<CategoryResponse> getAll(){
        var listAll = categoryRepository.findAll();
        List<CategoryResponse> result = new ArrayList<>();
        for (var category : listAll){
            if(category.getParent() == null) result.add(categoryMapper.toCategoryResponse(category));
        }

        return result;
    }

    public CategoryResponse getById(Integer id){
        var res = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_ID_NOT_FOUND)
        );

        return categoryMapper.toCategoryResponse(res);
    }

    public CategoryResponse update(Integer id, CategoryRequest request){

        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_ID_NOT_FOUND)
        );

        if(!category.getName().equalsIgnoreCase(request.getName()) &&
            categoryRepository.existsByName(request.getName())
        ) throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);

        if(!category.getAlias().equalsIgnoreCase(request.getAlias()) &&
                categoryRepository.existsByName(request.getAlias())
        ) throw new AppException(ErrorCode.CATEGORY_ALIAS_EXISTED);

        category.setName(request.getName());
        category.setAlias(request.getAlias());
        category.setDescription(request.getDescription());
        category.setEnabled(request.isEnabled());
        category.setImage(request.getImage());

        if(request.getParent() != null){
            int idParent = request.getParent();
            var parent = categoryRepository.findById(idParent).orElseThrow();
            category.setParent(parent);
        }

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public void delete(Integer id) throws IOException {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_ID_NOT_FOUND)
        );

        FileUploadUtil.deleteDir("category-images/" + id);
        categoryRepository.deleteById(id);
    }

    public Page<Category> getRootCategories(int pageNum, int pageSize, String sortField, String keyword){
        Sort sort = sortField != null ? Sort.by(sortField).ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        if(keyword == null){
            return categoryRepository.findRootCategories(pageable);
        } else {
            return categoryRepository.findCategoryByKeyWord(keyword, pageable);
        }
    }

}
