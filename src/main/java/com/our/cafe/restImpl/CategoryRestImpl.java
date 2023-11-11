package com.our.cafe.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.our.cafe.constents.CafeConstants;
import com.our.cafe.pojo.Category;
import com.our.cafe.rest.CategoryRest;
import com.our.cafe.service.CategoryService;
import com.our.cafe.utils.CafeUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryRestImpl implements CategoryRest {
	@Autowired
    CategoryService categoryService;
    @Override
    public ResponseEntity<String> addCategory(Map<String, String> requestMap) {
        try {
            return categoryService.addCategory(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getCategories(String filterValue) {
        try {
            return categoryService.getCategories(filterValue);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            return categoryService.updateCategory(requestMap);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
