package com.our.cafe.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.our.cafe.pojo.Bill;

import java.util.List;
import java.util.Map;

@RequestMapping("/bill")
public interface BillRest {
    @PostMapping("/generate-report")
    public ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap);
    @GetMapping("/get-bills")
    public ResponseEntity<List<Bill>> getBills();
    @PostMapping("/get-pdf")
    public ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap);
    @CrossOrigin(origins="http://localhost:4200")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Integer id);
}
