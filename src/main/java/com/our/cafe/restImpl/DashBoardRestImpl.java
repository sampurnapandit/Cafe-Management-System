package com.our.cafe.restImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.our.cafe.rest.DashboardRest;
import com.our.cafe.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DashBoardRestImpl implements DashboardRest {
	@Autowired
     DashboardService dashboardService;
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        try {
            return dashboardService.getCount();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
