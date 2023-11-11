package com.our.cafe.jwt;


import java.util.ArrayList;
import java.util.Objects;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.our.cafe.dao.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUsersDetailsService implements UserDetailsService{
	
	@Autowired
	private UserDao userDao;
	
	
	private com.our.cafe.pojo.User userDetails;
	
	
	    @Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		final org.slf4j.Logger  log =org.slf4j.LoggerFactory.getLogger(UserDetails.class);
		log.info("Inside loadUserByUsername{}", username);
		userDetails=userDao.findByEmailId(username);
		if(!Objects.isNull(userDetails))
		{  
			return new User(userDetails.getEmail(),userDetails.getPassword(),new ArrayList<>());	
		}
		else {
			throw new UsernameNotFoundException("user not found");
		}
	      }
	
	public com.our.cafe.pojo.User getUserDetail(){
		return userDetails;
	}
}
