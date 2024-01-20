package com.shefamma.shefamma.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.GeocodingResult;
import com.shefamma.shefamma.Repository.*;
import com.shefamma.shefamma.Repository.Account;
import com.shefamma.shefamma.Repository.ConstantCharges;
import com.shefamma.shefamma.config.AccountEntityUserDetails;
import com.shefamma.shefamma.config.GeocodingService;
import com.shefamma.shefamma.config.PinpointClass;
import com.shefamma.shefamma.entities.*;
import com.shefamma.shefamma.services.JwtServices;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@RestController
public class MyController {
    @Autowired
    private Host host;
    @Autowired
    private Guest guest;
    @Autowired
    private Item item;
    @Autowired
    private Meal meal;
    @Autowired
    private Order order;
    @Autowired
    private ConstantCharges constantCharges;
    @Autowired
    private Account account;
    @Autowired
    private DevBoy devBoy;
    @Autowired
    private SmsService smsService;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AccountEntity hostAccountEntity;
    @Autowired
    private PasswordChangeRequestPOJO passwordChangeRequest;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtServices jwtServices;
    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private PinpointClass pinpointClass;
    @Autowired
    private Capacity capacity;
    @Autowired
    private Pincode pincode;
    private String generatedOtp; 
    
    private LocalDateTime otpExpirationTime; 
    @Value("${map.host.radius}")
    private double radius;

@GetMapping("/guest/checkService")
public ResponseEntity<String> checkService(@RequestHeader String pinCode){
    boolean isAvailable = pincode.checkPincodeAvailability(pinCode);
    if(isAvailable) {
        return ResponseEntity.ok("Service is available in your area.Try updating your address in the Profile section.");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry, service is not available in your area.");
    }
}
    @PostMapping("/host")
    public HostEntity saveHost(@RequestBody HostEntity hostEntity) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(hostEntity.getAddressHost().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        String coordinates = String.format("%.6f,%.6f", latitude, longitude);
        hostEntity.setGeocode(coordinates);
        return host.saveHost(hostEntity);
    }
    @GetMapping("/")
    public String index() {
        return "API is running";
    }
    @PutMapping("/host")
    public HostEntity updateHost(@RequestBody HostEntity hostentity, @RequestParam String attributeName) {
        return host.update(hostentity.getUuidHost(), hostentity.getGeocode(), attributeName, hostentity);
    }
    @PostMapping("/guest/hosts")
    public ResponseEntity<?> getHostsWithinRadius(
            @RequestHeader String uuidGuest,
            @RequestBody(required = false) String address) {

            if (address == null || address.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Address is required.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode addressNode;
        try {
            addressNode = objectMapper.readTree(address);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid address format.");
        }

        JsonNode pinCodeNode = addressNode.path("address").path("pinCode");
        if (pinCodeNode.isMissingNode() || pinCodeNode.asText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Unable to detect pinCode");
        }
        String pinCode = pinCodeNode.asText();

        String guestAddress = String.format("%s, %s, %s, %s",
                addressNode.path("address").path("street").asText(),
                addressNode.path("address").path("city").asText(),
                addressNode.path("address").path("state").asText(),
                pinCode);

        GeocodingResult[] results;
        try {
            results = geocodingService.geocode(guestAddress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("We're having trouble locating your address. Please try again later.");
        }

        if (results.length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("We couldn't find a location for the address you provided. Please check your address for accuracy and ensure it's specific enough.");
        }

        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;

        boolean isAvailable = pincode.checkPincodeAvailability(pinCode);

        if (!isAvailable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service is not available in your area.");
        }

        List<HostCardEntity> hosts = host.findRestaurantsWithinRadius(latitude, longitude, radius);
        return ResponseEntity.ok(hosts);
    }


    @GetMapping("/guest/getHostUsingPk")
    public HostEntity getHostforGuest(@RequestHeader String uuidHost) {
        return host.getDataUsingPk(uuidHost);
    }
    @GetMapping("/host/getHostUsingPk")
    public HostEntity getHostUsingPk(@RequestHeader String uuidHost) {
        return host.getDataUsingPk(uuidHost);
    }

    
    @GetMapping("/guest/hosts/itemSearch")
    public List<HostCardEntity> getHostsItemSearchFilter(@RequestHeader("UUID") String uuidGuest, @RequestParam String address, @RequestParam("item") String itemValue, @RequestParam double radius) throws Exception {
        GeocodingResult[] results;
        if (Objects.equals(address, "address")) {
            results = geocodingService.geocode(guest.getGuestAddress(uuidGuest).convertToString());
        } else {
            results = geocodingService.geocode(address);
        }
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        return host.getHostsItemSearchFilter(latitude, longitude, radius, itemValue.toLowerCase());
    }
    @GetMapping("/guest/host/categoryFilter")
    public List<HostEntity> getHostsCategorySearchFilter(@RequestParam("category") String categoryValue) {
        return host.getHostsCategorySearchFilter(categoryValue);
    }


    @GetMapping("/host/ratingReview")
    public OrderEntity getHostRatingReview(@RequestBody HostEntity hostEntity) {
        return host.getHostRatingReview(hostEntity);
    }

    @PostMapping("/guest")
    public GuestEntity saveGuest(@RequestBody GuestEntity guestEntity) throws Exception {
         if (guestEntity.getGeocode() == null || guestEntity.getGeocode().isEmpty()) {
            GeocodingResult[] results = geocodingService.geocode(guestEntity.getAddressGuest().convertToString());
            double latitude = results[0].geometry.location.lat;
            double longitude = results[0].geometry.location.lng;
            String coordinates = String.format("%.6f,%.6f", latitude, longitude);
            guestEntity.setGeocode(coordinates);
        }
  if (guestEntity.getOfficeAddress() != null &&
                isNotEmpty(guestEntity.getOfficeAddress().getStreet()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getHouseName()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getCity()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getState()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getPinCode())) {

            String officeAddress = guestEntity.getOfficeAddress().convertToString();
            GeocodingResult[] resultsOffice = geocodingService.geocode(officeAddress);
            double officeLatitude = resultsOffice[0].geometry.location.lat;
            double officeLongitude = resultsOffice[0].geometry.location.lng;
            String officeCoordinates = String.format("%.6f,%.6f", officeLatitude, officeLongitude);
            guestEntity.setGeocodeOffice(officeCoordinates);
        }

        
        return guest.saveGuest(guestEntity);
    }

    
    private boolean isNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }
    @PutMapping("/guest/updateDetails")
    public GuestEntity updateDetails(@RequestBody GuestEntity guestEntity) throws Exception {
        System.out.println(guestEntity);
        // Always calculate geocode for office address if provided and not empty
        if (guestEntity.getOfficeAddress() != null &&
                isNotEmpty(guestEntity.getOfficeAddress().getStreet()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getHouseName()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getCity()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getState()) &&
                isNotEmpty(guestEntity.getOfficeAddress().getPinCode())) {

            String officeAddress = guestEntity.getOfficeAddress().convertToString();
            GeocodingResult[] resultsOffice = geocodingService.geocode(officeAddress);
            double officeLatitude = resultsOffice[0].geometry.location.lat;
            double officeLongitude = resultsOffice[0].geometry.location.lng;
            String officeCoordinates = String.format("%.6f,%.6f", officeLatitude, officeLongitude);
            guestEntity.setGeocodeOffice(officeCoordinates);
        }

        System.out.println("the data is guest entity - "+guestEntity);
        return guest.saveGuest(guestEntity);
}
    @GetMapping("/host/guest")
    public GuestEntity getGuest(@RequestHeader String uuidGuest, @RequestHeader String geocode) {
        return guest.getGuestAddress(uuidGuest, geocode);
    }
    @GetMapping("/guest/getGuestUsingPk")
    public GuestEntity getGuestUsingPk(@RequestHeader String uuidGuest) {
        return guest.getGuestUsingPk(uuidGuest);
    }
    
    @PutMapping("/guest")
    public GuestEntity updateGuest(@RequestBody GuestEntity guestentity, @RequestParam String attributeName) {
        
        return guest.updateGuest(guestentity.getUuidGuest(), guestentity.getGeocode(), attributeName, guestentity);
    }

    @PostMapping("/host/menuItem")
    public List<ItemEntity> createItems(@NotNull @RequestBody List<ItemEntity> itemEntities) {
        List<ItemEntity> items = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            String uuidItem = itemEntity.getUuidItem();

            itemEntity.setUuidItem(uuidItem.replace("host#", ""));

            ItemEntity savedItem = item.saveItem(itemEntity);
            items.add(savedItem);
        }
        
        return items;
    }

    @PutMapping("/host/menuItem")
    public ItemEntity updateItem(@RequestBody ItemEntity itementity) {
        return item.updateItem(itementity.getUuidItem(), itementity.getNameItem(), itementity);
    }
    @PutMapping("/host/menuItemAttribute")
    public ItemEntity updateItemAttribute(@RequestBody ItemEntity itementity, @RequestParam String attributeName) {
        return item.updateItemAttribute(itementity.getUuidItem(), itementity.getNameItem(),attributeName, itementity);
    }

    @GetMapping("/guest/host/menuItems")
    public List<ItemEntity> getItems(@RequestHeader String id) {
        try {
            String[] idSplit = id.split("#");
            List<ItemEntity> items = item.getItems("item#" + idSplit[1]);
            for (ItemEntity itemEntity : items) {
                
            }
            return items;
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching items", e);
            throw new RuntimeException("Error occurred while fetching items", e);
        }
    }

    @GetMapping("/host/menuItem")
    public ItemEntity getItem(@RequestBody ItemEntity itementity) {
        return item.getItem(itementity.getUuidItem(), itementity.getNameItem(), itementity);
    }
    @PostMapping("/host/meal")
    public ResponseEntity<MealEntity> createMeal(@RequestBody MealEntity mealEntity) {
        String uuid = mealEntity.getUuidMeal();

        if (uuid.startsWith("host")) {
            String newUuidMeal = uuid.replace("host", "item");
            mealEntity.setUuidMeal(newUuidMeal);
        }

        return meal.createMeal(mealEntity);
    }

    @PutMapping("/host/meal")
    public MealEntity updateMealAttribute(@RequestBody MealEntity mealEntity, @RequestParam String attributeName) {
        return meal.updateMealAttribute(mealEntity.getUuidMeal(), mealEntity.getMealType(),attributeName, mealEntity);
    }
    @GetMapping("/guest/host/mealItems")
    public List<MealEntity> getMealItems(@RequestHeader String id) {
        try {
            String[] idSplit = id.split("#");
            List<MealEntity> items = meal.getItems("item#" + idSplit[1]);
            for (MealEntity itemEntity : items) {
                
            }
            return items;
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.error("Error occurred while fetching items", e);
            throw new RuntimeException("Error occurred while fetching items", e);
        }
    }
    @GetMapping("/host/mealItem")
    public MealEntity getItem(@RequestBody MealEntity mealEntity) {
        return meal.getItem(mealEntity.getUuidMeal(), mealEntity.getMealType(), mealEntity);
    }
    


    @PostMapping("/host/capacity")
    public CapacityEntity createCapacity(@RequestBody CapacityEntity capacityEntity) {
        String uuid = capacityEntity.getUuidCapacity();

        if (uuid.startsWith("host#")) {
            String newUuidTime = uuid.replace("host#", "capacity#");
            capacityEntity.setUuidCapacity(newUuidTime);
        }

        
        return capacity.createCapacity(capacityEntity);
    }

    @GetMapping("/guest/host/capacity")
    public CapacityEntity getCapacity(@RequestHeader String id) {
        String[] idSplit = id.split("#");
        CapacityEntity capacity = this.capacity.getCapacity("capacity#" + idSplit[1]);
        
        return capacity;
    }

    @PostMapping("/guest/order")
    public ResponseEntity<?> createOrder(@RequestBody OrderEntity orderEntity, @RequestHeader String capacityUuid) throws Exception {
        
         String geoHost = orderEntity.getGeoHost();
        AdressSubEntity deliveryAddress = orderEntity.getDelAddress();
        String guestPinCode = deliveryAddress.getPinCode();

        String deliveryAddressStr = orderEntity.addressToString(orderEntity.getDelAddress());
        GeocodingResult[] resultsDelivery = geocodingService.geocode(deliveryAddressStr);
        double deliveryLatitude = resultsDelivery[0].geometry.location.lat;
        double deliveryLongitude = resultsDelivery[0].geometry.location.lng;
        String geoDelivery = String.format("%.6f,%.6f", deliveryLatitude, deliveryLongitude);

        if (!host.areAddressesWithinRadius(geoHost, geoDelivery, radius)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order cannot be placed due to long distance between you and cook.");
        }

        
        boolean isAvailable = pincode.checkPincodeAvailability(guestPinCode);
        if (!isAvailable) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service is not available at pincode - "+guestPinCode);
        }

        
        ResponseEntity<String> capacityUpdateResponse = capacity.updateCapacity(orderEntity.getMealType(), capacityUuid, Integer.parseInt(orderEntity.getNoOfServing()));

        
        if (capacityUpdateResponse.getStatusCode() == HttpStatus.OK) {
            OrderEntity createdOrder = order.createOrder(orderEntity);
            return ResponseEntity.ok(createdOrder);
        } else {
            
            return capacityUpdateResponse;
        }
    }

    @GetMapping("/host/orders")
    public List<OrderEntity> getHostOrders(@RequestHeader String hostID) {
        return order.getAllOrders(hostID,"gsi1");
    }

    @GetMapping("/host/ipOrders")
    public List<OrderEntity> getInProgressHostOrders(@RequestHeader String hostID) {
        return order.getInProgressOrders(hostID,"gsi1" );
    }
@GetMapping("/devBoy/ipOrders")
public List<OrderEntity> getInProgress(@RequestHeader String uuidDevBoy){
    return order.getInProgressAndPkdOrders(uuidDevBoy,"gsi2" );
}
    @GetMapping("/guest/orders")
    public List<OrderEntity> getGuestOrders(@RequestHeader String uuidOrder) {
        return order.getGuestOrders(uuidOrder);
    }

    @PutMapping("/guest/order")
    public ResponseEntity<String> updateOrder(@RequestBody OrderEntity orderEntity, @RequestParam String attributeName) {
        try {
            
            OrderEntity updatedOrder = order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName, orderEntity);
             
            if (updatedOrder != null) {
                
                
                return ResponseEntity.ok("Order updated successfully");
            } else {
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order");
            }
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }
    @PutMapping("/devBoy/updateOrder")
    public ResponseEntity<String> updateOrderDevBoy(@RequestBody OrderEntity orderEntity, @RequestParam String attributeName,@RequestParam String attributeName2) {
        try {
            
            OrderEntity updatedOrder1 = order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName, orderEntity);
            OrderEntity updatedOrder2 = order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName2, orderEntity);
            
            
            if (updatedOrder1 != null && updatedOrder2 != null) {
                return ResponseEntity.ok("Order updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order");
            }
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }
    @PutMapping("/guest/cancelOrder")
    public ResponseEntity<String> cancelOrderGuest(@RequestBody OrderEntity orderEntity, @RequestParam String attributeName, @RequestParam String attributeName2) {
        try {
            // Replace host# with capacity#
            String capacityUuid = orderEntity.getUuidHost().replace("host#", "capacity#");

            // First, attempt to increase the capacity
            ResponseEntity<String> capacityUpdateResponse = capacity.increaseCapacity(orderEntity.getMealType(), capacityUuid, Integer.parseInt(orderEntity.getNoOfServing()));
            if (!capacityUpdateResponse.getStatusCode().is2xxSuccessful()) {
                // If capacity update is not successful, return the capacity update's response
                return capacityUpdateResponse;
            }

            // Update order status and time only if capacity update is successful
            OrderEntity updatedOrder1 = order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName, orderEntity);
            OrderEntity updatedOrder2 = order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName2, orderEntity);

            // Check if both updates were successful
            if (updatedOrder1 != null && updatedOrder2 != null) {
                return ResponseEntity.ok("Order updated and capacity adjusted successfully.");
            } else {
                // Handle partial or complete failure of order updates
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order status or time.");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number format: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request: " + e.getMessage());
        }
    }
    @PutMapping("/host/payment")
    public void updatePayment(@RequestBody OrderEntity orderEntity) {

        order.updatePayment(orderEntity);
    }
    @GetMapping("/devBoy/ipDevBoyOrders")
    public List<OrderWithAddress> ipDevBoyOrders(@RequestHeader String uuidDevBoy) {
        return order.getInProgressDevBoyOrders(uuidDevBoy);
    }
    @GetMapping("/devBoy/devBoyOrders")
    public List<OrderEntity> devBoyOrders(@RequestHeader String uuidDevBoy) {
        return order.getAllOrders(uuidDevBoy,"gsi2");

    }
 @PostMapping("/generateOtp")
    public ResponseEntity<String> generateOtp(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        String email = payload.get("email");

        
        if (phone == null && email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please provide either phone or email.");
        }

        
        if (phone != null) {
            try {
                userDetailsService.loadUserByUsername(phone);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists for phone: " + phone);
            } catch (UsernameNotFoundException ignored) {
            }
        }

        
        if (email != null) {
            try {
                userDetailsService.loadUserByUsername(email);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists for email: " + email);
            } catch (UsernameNotFoundException ignored) {
            }
        }

        
        generatedOtp = PinpointClass.generateOTPWithExpiration();
        otpExpirationTime = LocalDateTime.now().plusSeconds(PinpointClass.getOtpExpirationSeconds());

        
        if (phone != null) {
            ResponseEntity<SmsService.SmsResponse> response = smsService.sendOtpSms(phone, generatedOtp);
            if (response.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(response.getStatusCode()).body("Failed to send OTP SMS");
            }
        }


        return ResponseEntity.ok("OTP generated and sent successfully.");
    }


    @PostMapping("/otpPhone")
    public ResponseEntity<?> verifySms(@RequestBody OtpVerificationClass otpVerificationClass) {
        return verifyOtp(otpVerificationClass.getPhoneOtp());
    }

    @PostMapping("/otpEmail")
    public ResponseEntity<?> verifyEmail(@RequestBody OtpVerificationClass otpVerificationClass) {
        return verifyOtp(otpVerificationClass.getEmailOtp());
    }
    private ResponseEntity<?> verifyOtp(String userOtp) {
        if (isOTPExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP has expired");
        }
        if (Objects.equals(userOtp, generatedOtp)) {
            return ResponseEntity.ok("SUCCESS");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
    }

    private boolean isOTPExpired() {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(otpExpirationTime);
    }

@PostMapping("/admin/addCharges")
public ResponseEntity<String> addCharges(@RequestBody ConstantChargesEntity constantChargesEntity) {
    return constantCharges.addCharges(constantChargesEntity);
}


    @GetMapping("/guest/getCharges")
    public ResponseEntity<ConstantChargesEntity> getCharges() {
        return constantCharges.getCharges();
    }
    @PostMapping("/hostSignup")
    public ResponseEntity<?> getUser(@RequestBody AccountEntity hostEntity) {
        try {
            userDetailsService.loadUserByUsername(hostEntity.getPhone());
            String errorMessage = "User already exists for phone: " + hostEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            
            AccountEntity existingUser = account.findUserByEmail(hostEntity.getEmail());
            if (existingUser != null) {
                String errorMessage = "User already exists for email: " + hostEntity.getEmail();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            }
           hostEntity.setRole("host");
            
            AccountEntity newAccountEntity = account.saveSignup(hostEntity, "host");

            
            String token = jwtServices.generateToken(hostEntity.getPhone());
            String uuidHost = account.storeHostUuid();
            String hostTimestamp = account.storeTimestamp();
            Map<String, Object> response = new HashMap<>();
            response.put("uuidHost", uuidHost);
            response.put("token", token);
            response.put("timeStamp", hostTimestamp);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/hostLogin")
    public ResponseEntity<?> hostLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getPhone());
                String uuidHost = account.storeHostUuid();
                String timestamp = account.storeTimestamp();

                Map<String, Object> response = new HashMap<>();
                response.put("uuidHost", uuidHost);
                response.put("token", token);
                response.put("timeStamp", timestamp);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    
    

    @PostMapping("/guestLogin")
    public ResponseEntity<?> guestLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getPhone());
                String uuidGuest = account.storeGuestUuid();
                String timestamp = account.storeTimestamp();
                GuestEntity guestDetails = guest.getGuestUsingPk(uuidGuest);

                Map<String, Object> response = new HashMap<>();
                response.put("uuidGuest", uuidGuest);
                response.put("token", token);
                response.put("timeStamp", timestamp);

                // Check if guestDetails is null
                if (guestDetails == null) {
                    response.put("message", "User details not available.");
                } else {
                    response.put("guestDetails", guestDetails);
                }

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account not found");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    @PostMapping("/guestSignup")
    public ResponseEntity<?> getUserGuest(@RequestBody AccountEntity guestEntity) {
        try {
            userDetailsService.loadUserByUsername(guestEntity.getPhone());

            String errorMessage = "User already exists for phone: " + guestEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {

            guestEntity.setRole("guest");
            
            AccountEntity newAccountEntity = account.saveSignup(guestEntity, "guest");

            
            String token = jwtServices.generateToken(guestEntity.getPhone());
            String uuidGuest = account.storeGuestUuid();
            String timestamp = account.storeTimestamp();
            GuestEntity guestDetails=guest.getGuestUsingPk(uuidGuest);

            Map<String, Object> response = new HashMap<>();
            response.put("uuidGuest", uuidGuest);
            response.put("token", token);
            response.put("timeStamp", timestamp);
            response.put("guestDetails", guestDetails);
            return ResponseEntity.ok(response);
        }
    }

    
    

    @PostMapping("/devBoySignup")
    public ResponseEntity<?> getUserDev(@RequestBody AccountEntity devBoyEntity) {
        try {
            userDetailsService.loadUserByUsername(devBoyEntity.getPhone());

            String errorMessage = "User already exists for phone: " + devBoyEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            devBoyEntity.setRole("devBoy");
            
            AccountEntity newAccountEntity = account.saveSignup(devBoyEntity, "devBoy");

            
            String token = jwtServices.generateToken(devBoyEntity.getPhone());
            String uuidDevBoy = account.storeDevBoyUuid();
            String timestamp = account.storeTimestamp();
            Map<String, Object> response = new HashMap<>();
            response.put("uuidDevBoy", uuidDevBoy);
            response.put("token", token);
            response.put("timeStamp", timestamp);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/devBoyLogin")
    public ResponseEntity<?> devBoyLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                AccountEntityUserDetails userDetails = (AccountEntityUserDetails) authentication.getPrincipal();
                String userRole = userDetails.getRole(); 

                if (!userRole.equals("devBoy")) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied for role: " + userRole);
                }

                String token = jwtServices.generateToken(authRequest.getPhone());
                String uuidDevBoy = account.storeDevBoyUuid();
                String timestamp = account.storeTimestamp();

                Map<String, Object> response = new HashMap<>();
                response.put("uuidDevBoy", uuidDevBoy);
                response.put("token", token);
                response.put("timeStamp", timestamp);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
    @PostMapping("/devBoy/createDevBoy")
    public ResponseEntity<DevBoyEntity> saveDevBoy(@RequestBody DevBoyEntity devBoyEntity) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(devBoyEntity.getLocationDevBoy().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        String coordinates = String.format("%.6f,%.6f", latitude, longitude);
        devBoyEntity.setGeocode(coordinates);

        DevBoyEntity savedEntity = devBoy.saveDevBoy(devBoyEntity);
        return ResponseEntity.ok(savedEntity);
    }

@PostMapping("/admin/Login")
public ResponseEntity<?> adminLogin(@RequestBody AccountEntity authRequest) {
    try {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            AccountEntityUserDetails userDetails = (AccountEntityUserDetails) authentication.getPrincipal();
            String userRole = userDetails.getRole(); 

            if (!userRole.equals("admin")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied for role: " + userRole);
            }

            String token = jwtServices.generateToken(authRequest.getPhone());
            String uuidAdmin = account.storeAdminUuid();
            String timestamp = account.storeTimestamp();

            Map<String, Object> response = new HashMap<>();
            response.put("uuidAdmin", uuidAdmin);
            response.put("token", token);
            response.put("timeStamp", timestamp);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    } catch (AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
    }
}

    @PutMapping("/devBoy")
public DevBoyEntity updateDevBoy(@RequestBody DevBoyEntity hostentity, @RequestParam String attributeName) {
    return devBoy.update(hostentity.getUuidDevBoy(), hostentity.getGeocode(), attributeName, hostentity);
}
    @PostMapping("/host/changePassword")
    public ResponseEntity<?> changePasswordHost(@RequestBody PasswordChangeRequestPOJO passwordChangeRequest) {
        return processChangePassword(passwordChangeRequest);
    }

    @PostMapping("/guest/changePassword")
    public ResponseEntity<?> changePasswordGuest(@RequestBody PasswordChangeRequestPOJO passwordChangeRequest) {
        return processChangePassword(passwordChangeRequest);
    }

    private ResponseEntity<?> processChangePassword(PasswordChangeRequestPOJO passwordChangeRequest) {
        try {
            
            boolean isPasswordCorrect = account.isPasswordCorrect(passwordChangeRequest.getPhone(),
                    passwordChangeRequest.getOldPassword());

            if (!isPasswordCorrect) {
                String errorMessage = "Incorrect original password.";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse(false, errorMessage));
            }

            
            String newPassword = passwordChangeRequest.getNewPassword();
            account.changePassword(passwordChangeRequest.getPhone(), passwordChangeRequest.getTimeStamp(), newPassword);

            return ResponseEntity.ok(createResponse(true, "Password changed successfully"));

        } catch (Exception e) {
            String errorMessage = "An error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse(false, errorMessage));
        }
    }

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }

}
