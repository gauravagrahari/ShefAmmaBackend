package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.Repository.*;
import com.shefamma.shefamma.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AdminDashboardController {
    @Autowired
    AdminDashboard adminDashboard;
    @Autowired
    private Order order;
    @Autowired
    private Meal meal;
    @Autowired
    private Host host;
    @Autowired
    private Pincode pincode;
    @Autowired
    private DevBoy devBoy;
    @Autowired
    private ConstantCharges constantCharges;
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
@GetMapping("/admin/getCharges")
public ResponseEntity<ConstantChargesEntity> getCharges() {
    return constantCharges.getCharges();
}
    @PutMapping("/admin/updateCharges")
    public ResponseEntity<String> updateCharges(@RequestBody ConstantChargesEntity constantChargesEntity) {
        return constantCharges.updateCharges(constantChargesEntity);
    }

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
    @GetMapping("/admin/getAllOrdersBetweenTimestamp")
    public List<OrderEntity> getOrdersBetweenTimestampsForMultipleIds(@RequestParam List<String> ids, @RequestParam String startTime, @RequestParam String EndTime) {
        return order.getOrdersBetweenTimestampsForMultipleIds(ids, "gsi1", startTime,EndTime);
    }
    @GetMapping("/admin/getOrdersBetweenTimestamp")
    public List<OrderEntity> getOrdersBetweenTimestamp(@RequestParam String startTime, @RequestParam String EndTime) {
        return order.getOrdersBetweenTimestamps("gsi1", startTime,EndTime);
    }

//    ************************for meal****************************
    @GetMapping("/admin/host/mealItems")
    public List<MealEntity> getMealItems(@RequestHeader String id) {
        try {
            String[] idSplit = id.split("#");
            return meal.getItems("item#" + idSplit[1]);
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching items", e);
            throw new RuntimeException("Error occurred while fetching items", e);
        }
    }
    @PutMapping("/admin/host/meal")
    public MealEntity updateMealAttribute(@RequestBody MealEntity mealEntity, @RequestParam String attributeName) {
        return meal.updateMealAttribute(mealEntity.getUuidMeal(), mealEntity.getNameItem(),attributeName, mealEntity);
    }
    @PostMapping("/admin/meal")
    public ResponseEntity<MealEntity> createMeal(@RequestBody MealEntity mealEntity) {
        return meal.createMeal(mealEntity);
    }

//    **************************************************************
@PostMapping("/admin/deactivatePincode")
public ResponseEntity<String> deactivatePincode(@RequestHeader String pin) {
    return pincode.deactivatePincode(pin);
}
    @PostMapping("/admin/updatePincodeStatus")
    public ResponseEntity<String> updatePincodeStatus(@RequestBody PincodeEntity pincodeEntity) {
        return pincode.updatePincodeStatus(pincodeEntity);
    }
    @PostMapping("/admin/addPincode")
    public PincodeEntity addPincode(@RequestBody PincodeEntity pincodeEntity) {
        return pincode.addPincode(pincodeEntity);
    }
    @GetMapping("/admin/checkService")
    public ResponseEntity<String> checkServiceAdmin(@RequestHeader String pinCode){
        boolean isAvailable = pincode.checkPincodeAvailability(pinCode);
        if(isAvailable) {
            return ResponseEntity.ok("Service is available at " +pinCode);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry, service is not available in your area.");
        }
    }
    @GetMapping("/admin/getAllPincodes")
    public ResponseEntity<List<PincodeEntity>> getAllPincodes() {

        List<PincodeEntity> pincodes = pincode.getAllPincodes();
        if(pincodes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }
        return ResponseEntity.ok(pincodes);
    }


}
