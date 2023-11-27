package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.AdminDashboard;
import com.shefamma.shefamma.Repository.DevBoy;
import com.shefamma.shefamma.Repository.Host;
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
    @Autowired
    private Host host;
    @Autowired
    private DevBoy devBoy;
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
    @GetMapping("/admin/getAllOrdersByStatus")
    public List<OrderEntity> getAllOrdersByStatus(@RequestParam List<String> ids, @RequestParam String gsiName, @RequestParam String status) {
        return order.getAllOrdersByStatus(ids, gsiName, status);
    }

    @PutMapping("/admin/updateHost")
    public HostEntity updateHost(@RequestBody HostEntity hostentity, @RequestParam String attributeName) {
        return host.update(hostentity.getUuidHost(), hostentity.getGeocode(), attributeName, hostentity);
    }
    @PutMapping("/admin/updateDevBoy")
    public DevBoyEntity updateDevBoy(@RequestBody DevBoyEntity hostentity, @RequestParam String attributeName) {
        return devBoy.update(hostentity.getUuidDevBoy(), hostentity.getGeocode(), attributeName, hostentity);
    }
    @PutMapping("/admin/updateOrder")
    public OrderEntity updateOrder(@RequestBody OrderEntity orderData, @RequestParam String attributeName) {
        return order.updateOrder(orderData.getUuidOrder(), orderData.getTimeStamp(), attributeName, orderData);
    }

    @PutMapping("/admin/assignDev")
    public ResponseEntity<String> assignDev(@RequestBody AdminDashboardEntity adminDashboardEntity) {
        return null;
    }
}
