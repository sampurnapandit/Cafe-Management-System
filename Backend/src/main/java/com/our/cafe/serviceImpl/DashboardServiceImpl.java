package com.our.cafe.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.our.cafe.dao.BillDao;
import com.our.cafe.dao.CategoryDao;
import com.our.cafe.dao.ProductDao;
import com.our.cafe.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
	 @Autowired
     CategoryDao categoryDao;
	 @Autowired
     ProductDao productDao;
	 @Autowired
     BillDao billDao;
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("category", categoryDao.count());
            map.put("product", productDao.count());
            map.put("bill", billDao.count());
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
