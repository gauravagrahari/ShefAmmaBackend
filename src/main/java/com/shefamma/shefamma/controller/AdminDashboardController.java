package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.AdminDashboard;
import com.shefamma.shefamma.Repository.Order;
import com.shefamma.shefamma.entities.AdminDashboardEntity;
import com.shefamma.shefamma.entities.DevBoyEntity;
import com.shefamma.shefamma.entities.HostEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminDashboardController {
    @Autowired
    AdminDashboard adminDashboard;
    @Autowired
    private Order order;
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
//    @PostMapping("/admin/signup") // Changed the mapping URL to "/admin/signup"
//    public ResponseEntity<String> adminSignup(@RequestBody AdminDashboardEntity adminDashboardEntity) {
//        return adminDashboard.saveSignup(adminDashboardEntity);
//    }

    @PostMapping("/admin/getAllHosts") // Changed the mapping URL to "/admin/signup"
    public List<HostEntity> getAllHosts() {
      return adminDashboard.getAllHosts();
    }
    @PostMapping("/admin/getAllDevBoys") // Changed the mapping URL to "/admin/signup"
    public List<DevBoyEntity> getAllDevBoys() {
        return adminDashboard.getAllDevBoys();
    }

    @GetMapping("/admin/devOrders")
    public List<OrderEntity> devOrders(@RequestHeader String id) {
        return order.getAllOrders(id,"gsi2");
    }
    @GetMapping("/admin/hostOrders")
    public List<OrderEntity> getHostOrders(@RequestHeader String hostID) {
        return order.getAllOrders(hostID,"gsi1");
    }
    @GetMapping("/admin/getOrdersByStatus")
    public List<OrderEntity> getOrdersByStatus(@RequestHeader String id,@RequestParam String gsiName,@RequestParam String status){
        return order.getOrdersByStatus(id,gsiName,status);
    }
    @GetMapping("/admin/getHosts")
    public ResponseEntity<String> getHosts() {
        return null;
    }
    @PutMapping("/admin/assignDev")
    public ResponseEntity<String> assignDev(@RequestBody AdminDashboardEntity adminDashboardEntity) {
        return null;
    }
}
