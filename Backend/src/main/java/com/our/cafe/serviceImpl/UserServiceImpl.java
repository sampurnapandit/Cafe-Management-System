package com.our.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.our.cafe.constents.CafeConstants;
import com.our.cafe.dao.UserDao;
import com.our.cafe.jwt.CustomerUsersDetailsService;
import com.our.cafe.jwt.JwtFilter;
import com.our.cafe.jwt.JwtUtil;
import com.our.cafe.pojo.User;
import com.our.cafe.service.UserService;
import com.our.cafe.utils.CafeUtils;
import com.our.cafe.utils.EmailUtils;
import com.our.cafe.wrapper.UserWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	@Autowired
    UserDao userDao;
	@Autowired
    AuthenticationManager authenticationManager;
	@Autowired
    CustomerUsersDetailsService customerUsersDetailsService;
	@Autowired
    JwtUtil jwtUtil;
	@Autowired
    JwtFilter jwtFilter;
	@Autowired
    EmailUtils emailUtils;
    @Override
    public ResponseEntity<String> signup(Map<String, String> requestMap) {
    	 final org.slf4j.Logger  log =org.slf4j.LoggerFactory.getLogger(UserDetails.class);
        log.info("Inside signup {}", requestMap);
        try {
            if(validateSignUpMap(requestMap)){
                User user = userDao.findByEmailId(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
    	 final org.slf4j.Logger  log =org.slf4j.LoggerFactory.getLogger(UserDetails.class);
        log.info("Inside login {}",requestMap);
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if(authentication.isAuthenticated()){
                if(customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),customerUsersDetailsService.getUserDetail().getRole()) + "\"}",HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\""+"Wait for admin approval." + "\"}", HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception exception){
            log.error("{}", exception);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Bad credentials." + "\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try {
            if(jwtFilter.isAdmin()) return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            else return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                Optional<User> user = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!user.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    this.sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User id does not exist", HttpStatus.NO_CONTENT);
                }
            } else return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(jwtFilter.getCurrentUser());
            if(user != null){
                if(user.getPassword().equalsIgnoreCase(requestMap.get("oldPassword"))){
                    user.setPassword(requestMap.get("newPassword"));
                    userDao.save(user);
                    return CafeUtils.getResponseEntity("Password has been updated successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Old password is not true", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception exception){
            exception.printStackTrace();
        }
    return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", user.getPassword());
            return CafeUtils.getResponseEntity("Check your email for credentials", HttpStatus.BAD_GATEWAY);
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status!=null && status.equalsIgnoreCase("true"))
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved","User:- " + user + "\n is approved by \nADMIN:- " +jwtFilter.getCurrentUser(), allAdmin);
        else
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled","User:- " + user + "\n is disabled by \nADMIN:- " +jwtFilter.getCurrentUser(), allAdmin);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        return requestMap.containsKey("name")
                && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }
    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
}
