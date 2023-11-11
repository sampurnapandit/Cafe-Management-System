package com.our.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.our.cafe.constents.CafeConstants;
import com.our.cafe.dao.ProductDao;
import com.our.cafe.jwt.CustomerUsersDetailsService;
import com.our.cafe.jwt.JwtFilter;
import com.our.cafe.pojo.Category;
import com.our.cafe.pojo.Product;
import com.our.cafe.service.ProductService;
import com.our.cafe.utils.CafeUtils;
import com.our.cafe.utils.EmailUtils;
import com.our.cafe.wrapper.ProductWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	@Autowired
     ProductDao productDao;
	@Autowired
     JwtFilter jwtFilter;
	 @Autowired
	  CustomerUsersDetailsService customerUserDetailsService;

	    @Autowired
	    EmailUtils emailUtil;
    @Override
    public ResponseEntity<String> addProduct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap, false)){
                    productDao.save(getProductFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Product was added successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        try {
            return new ResponseEntity<>(productDao.getAllProducts(),HttpStatus.OK);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                if(this.validateProductMap(requestMap, true)){
                    Optional<Product> productDaoById = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(productDaoById.isPresent()){
                        Product productFromMap = this.getProductFromMap(requestMap, true);
                        productFromMap.setStatus(productDaoById.get().getStatus());
                        productDao.save(productFromMap);
                        return CafeUtils.getResponseEntity("Product was updated successfully", HttpStatus.OK);
                    } else return CafeUtils.getResponseEntity("Product with id " + requestMap.get("id") + " does not exists", HttpStatus.NOT_FOUND);
                } else return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<Product> product = productDao.findById(id);
                if(product.isPresent()){
                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Product was deleted successfully", HttpStatus.OK);
                } else return CafeUtils.getResponseEntity("Product with id " + id + " does not exists", HttpStatus.NOT_FOUND);
            } else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<Product> product = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if(product.isPresent()){
                    productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product status was updated successfully", HttpStatus.OK);
                } else return CafeUtils.getResponseEntity("Product with id " + requestMap.get("id") + " does not exists", HttpStatus.NOT_FOUND);
            } else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory(id), HttpStatus.OK);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById(id), HttpStatus.OK);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(id, null, id, null, null, id, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        } else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            } else return !validateId;
        }
        return false;
    }
}
