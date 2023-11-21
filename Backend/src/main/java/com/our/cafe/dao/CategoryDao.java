package com.our.cafe.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.our.cafe.pojo.Category;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category, Integer> {
    List<Category> getAllCategory();
}
