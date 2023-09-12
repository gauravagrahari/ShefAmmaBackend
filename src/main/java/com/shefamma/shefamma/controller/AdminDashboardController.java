package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.AdminDashboard;
import com.shefamma.shefamma.entities.AdminDashboardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminDashboardController {
    @Autowired
    AdminDashboard adminDashboard;
    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody AdminDashboardEntity adminDashboardEntity) {
        ResponseEntity<String> response = adminDashboard.login(adminDashboardEntity);
        if(response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("token",response.getBody());
            responseBody.put("message", "Login successful for id: " + adminDashboardEntity.getId());
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }
        return ResponseEntity.status(response.getStatusCode()).body(Collections.singletonMap("message", response.getBody()));
    }
    @PostMapping("/admin/signup") // Changed the mapping URL to "/admin/signup"
    public ResponseEntity<String> adminSignup(@RequestBody AdminDashboardEntity adminDashboardEntity) {
        return adminDashboard.saveSignup(adminDashboardEntity);
    }
    @PostMapping("/admin/createDev") // Changed the mapping URL to "/admin/signup"
    public ResponseEntity<String> createDev(@RequestBody AdminDashboardEntity adminDashboardEntity) {
        return null;
    }
   @GetMapping("/admin/newOrders")
   public ResponseEntity<String> newOrders() {
        return null;
    }
    @PutMapping("/admin/assignDev")
    public ResponseEntity<String> assignDev(@RequestBody AdminDashboardEntity adminDashboardEntity) {
        return null;
    }
    @GetMapping("/admin/devOrders")
    public ResponseEntity<String> devOrders() {
        return null;
    }
    @GetMapping("/admin/hostOrders")
    public ResponseEntity<String> hostOrders() {
        return null;
    }
    @GetMapping("/admin/getHosts")
    public ResponseEntity<String> getHosts() {
        return null;
    }
}
