package com.shefamma.shefamma.controller;

import com.google.maps.model.GeocodingResult;
import com.shefamma.shefamma.Repository.*;
import com.shefamma.shefamma.Repository.Account;
import com.shefamma.shefamma.Repository.ConstantCharges;
import com.shefamma.shefamma.config.GeocodingService;
import com.shefamma.shefamma.config.PinpointClass;
import com.shefamma.shefamma.entities.*;
import com.shefamma.shefamma.services.JwtServices;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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
    private TimeSlot timeSlot;
    @Autowired
    private Order order;
    @Autowired
    private ConstantCharges constantCharges;
    @Autowired
    private Account account;

    @Autowired
    private HomeEntity homeEntity;
    @Autowired
//    @Qualifier("hostUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
//    @Qualifier("userDetailsServiceGuest")
//    private UserDetailsService userDetailsServiceGuest;
//    @Autowired
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
    private String generatedOtp; // Store the generated OTP here
    //    ---------------------------------------------------------
    private LocalDateTime otpExpirationTime; // Store the expiration time here

    @CrossOrigin(origins = "*")
    @PostMapping("/home")
    public String home(@RequestBody String message) {
        System.out.println(message);
        return homeEntity.getFName();
    }
// ------------------------------------------------------------------------------------------------------
// **************************************Host controllers******************************************
// ------------------------------------------------------------------------------------------------------

    // @CrossOrigin(origins = "*")
    @PostMapping("/host")
    public HostEntity saveHost(@RequestBody HostEntity hostEntity) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(hostEntity.getAddressHost().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        String coordinates = String.format("%.6f,%.6f", latitude, longitude);
        hostEntity.setGeocode(coordinates);
        return host.saveHost(hostEntity);
    }

    //the response might need change as we can only send the editted attribute, it would be better
    ///host?attribute=val
    @PutMapping("/host")
    public HostEntity updateHost(@RequestBody HostEntity hostentity, @RequestParam String attributeName) {
        return host.update(hostentity.getUuidHost(), hostentity.getGeocode(), attributeName, hostentity);
    }
    ///guest/hosts?radius=val
    @GetMapping("/guest/hosts")
    public List<HostCardEntity> getHostsWithinRadius(@RequestHeader("UUID") String uuidGuest, @RequestParam double radius) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(guest.getGuest(uuidGuest).convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        List<HostCardEntity> x = host.findRestaurantsWithinRadius(latitude, longitude, radius);
        System.out.println(x);
// return host.findRestaurantsWithinRadius(latitude, longitude, radius);
        return x;
    }
    @GetMapping("/guest/getHostUsingPk")
    public HostEntity getHostforGuest(@RequestHeader String uuidHost) {
        return host.getDataUsingPk(uuidHost);
    }
    @GetMapping("/host/getHostUsingPk")
    public HostEntity getHostUsingPk(@RequestHeader String uuidHost) {
        return host.getDataUsingPk(uuidHost);
    }

    // /guest/host?item=val&radius=val
    @GetMapping("/guest/hosts/itemSearch")
    public List<HostCardEntity> getHostsItemSearchFilter(@RequestHeader("UUID") String uuidGuest, @RequestParam String address, @RequestParam("item") String itemValue, @RequestParam double radius) throws Exception {
        GeocodingResult[] results;
        if (Objects.equals(address, "address")) {
            results = geocodingService.geocode(guest.getGuest(uuidGuest).convertToString());
        } else {
            results = geocodingService.geocode(address);
        }
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        return host.getHostsItemSearchFilter(latitude, longitude, radius, itemValue.toLowerCase());
    }
    //    not required controllers******************************************
//    .....some mistake, here list shall be returned
//    @GetMapping("/guest/hosts")
//    public HostEntity getHosts(@RequestBody HostEntity hostentity) {
//        return host.getHost(hostentity.getUuidHost(), hostentity.getNameHost());
//    }
    //        /guest/host?category=val
    @GetMapping("/guest/host/categoryFilter")
    public List<HostEntity> getHostsCategorySearchFilter(@RequestParam("category") String categoryValue) {
        return host.getHostsCategorySearchFilter(categoryValue);
    }

    //        /guest/host?startTime=val&endTime=val
    @GetMapping("/guest/host/slotFilter")
    public List<HostEntity> getHostsTimeSlotSearchFilter(@RequestParam String startTime, @RequestParam String endTime, @RequestParam String timeDuration) {
        return host.getHostsTimeSlotSearchFilter(Integer.parseInt(startTime), Integer.parseInt(endTime), timeDuration);
    }

    @GetMapping("/host/ratingReview")
    public OrderEntity getHostRatingReview(@RequestBody HostEntity hostEntity) {
        return host.getHostRatingReview(hostEntity);
    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Guest controllers******************************************
//    ------------------------------------------------------------------------------------------------------
//@PostMapping("/guest")
//public GuestEntity saveGuest(
//        @RequestPart("uuidGuest") String uuidGuest,
//        @RequestPart("geocode") String geocode,
//        @RequestPart("name") String name
//        @RequestPart("DP") MultipartFile DP
//        @RequestPart("addressGuest") String addressGuestJson
//)
//        throws IOException {
//
//    ObjectMapper objectMapper = new ObjectMapper();
//    AdressSubEntity addressGuest = objectMapper.readValue(addressGuestJson, AdressSubEntity.class);
//
//    GuestEntity guestEntity = new GuestEntity();
//    guestEntity.setUuidGuest(uuidGuest);
//    guestEntity.setGeocode(geocode);
//    guestEntity.setName(name);
//    guestEntity.setDP(DP.getBytes());
//    guestEntity.setAddressGuest(addressGuest);
//
//    return guest.saveGuest(guestEntity);
//}
//    @RequestHeader("Authorization") String token
    @PostMapping("/guest")
    public GuestEntity saveGuest(@RequestBody GuestEntity guestEntity) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(guestEntity.getAddressGuest().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        String coordinates = String.format("%.6f,%.6f", latitude, longitude);
        guestEntity.setGeocode(coordinates);
        System.out.println(guestEntity);
        String officeAddress = String.valueOf(guestEntity.getOfficeAddress());
        if (!Objects.equals(officeAddress, null) && !officeAddress.isEmpty()) {
            GeocodingResult[] resultsOffice = geocodingService.geocode(officeAddress.toString());
            double officeLatitude = resultsOffice[0].geometry.location.lat;
            double officeLongitude = resultsOffice[0].geometry.location.lng;
            String officeCoordinates = String.format("%.6f,%.6f", officeLatitude, officeLongitude);
            guestEntity.setGeocodeOffice(officeCoordinates);
        }
        return guest.saveGuest(guestEntity);
    }
@PutMapping("/guest/updateDetails")
public GuestEntity updateDetails(@RequestBody GuestEntity guestEntity) throws Exception {
        return saveGuest(guestEntity);
}
    @GetMapping("/host/guest")
    public GuestEntity getGuest(@RequestHeader String uuidGuest, @RequestHeader String geocode) {
        return guest.getGuest(uuidGuest, geocode);
    }
    @GetMapping("/guest/getGuestUsingPk")
    public GuestEntity getGuestUsingPk(@RequestHeader String uuidGuest  ) {
        return guest.getGuestUsingPk(uuidGuest);
    }

    ///guest?attributeName=val
    @PutMapping("/guest")
    public GuestEntity updateGuest(@RequestBody GuestEntity guestentity, @RequestParam String attributeName) {
        System.out.println(attributeName);
        return guest.updateGuest(guestentity.getUuidGuest(), guestentity.getGeocode(), attributeName, guestentity);
    }
//    @PutMapping("/guestEdit")
//    public GuestEntity updateGuest(@RequestBody GuestEntity guestentity, @RequestParam List<String> fields) {
//        System.out.println(fields);
//        return guest.updateGuest(guestentity.getUuidGuest(), guestentity.getGeocode(), fields, guestentity);
//    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Item controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/menuItem")
    public List<ItemEntity> createItems(@NotNull @RequestBody List<ItemEntity> itemEntities) {
        List<ItemEntity> items = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            String uuidItem = itemEntity.getUuidItem();
//            if (uuidItem.startsWith("host#")) {
            itemEntity.setUuidItem(uuidItem.replace("host#", ""));
//            }
            ItemEntity savedItem = item.saveItem(itemEntity);
            items.add(savedItem);
        }
        System.out.println(items);
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
    //    @GetMapping("/guest/host/menuItems")
//    public List<ItemEntity> getItems(@RequestHeader String id) {
//        String[] idSplit = id.split("#");
//        List<ItemEntity> x=item.getItems("item#" + idSplit[1]);
//        System.out.println(x);
//        return x;
////        return item.getItems("item#" + idSplit[1]);
//    }
    @GetMapping("/guest/host/menuItems")
    public List<ItemEntity> getItems(@RequestHeader String id) {
        try {
            String[] idSplit = id.split("#");
            List<ItemEntity> items = item.getItems("item#" + idSplit[1]);
            for (ItemEntity itemEntity : items) {
                System.out.println(itemEntity);
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



    //    ------------------------------------------------------------------------------------------------------
//    **************************************Meal controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/meal")
    public ResponseEntity<MealEntity> createMeal(@RequestBody MealEntity mealEntity) {
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
                System.out.println(itemEntity);
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
    //    ------------------------------------------------------------------------------------------------------
//    **************************************TimeSlot controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/timeSlot")
    public TimeSlotEntity createTimeSlot(@RequestBody TimeSlotEntity timeentity) {
        // Replace "host" with "time" and append a random number
        String newUuidTime = timeentity.getUuidTime().replace("host#", "");
        // Update the TimeSlotEntity with the new UUID
        timeentity.setUuidTime(newUuidTime);
        System.out.println(timeentity);
        return timeSlot.saveSlotTime(timeentity);
    }

    @GetMapping("/guest/host/timeSlot")
    public TimeSlotEntity getTimeSlot(@RequestHeader String id) {
        String[] idSplit = id.split("#");
        TimeSlotEntity x = timeSlot.getTimeSlot("time#" + idSplit[1]);
        System.out.println(x);
        return x;
//        return timeSlot.getTimeSlot("time#" + idSplit[1]);
    }

    @PutMapping("/host/timeSlot")
    public TimeSlotEntity updateTimeSlot(@RequestBody TimeSlotEntity timeentity) {
        return timeSlot.updateTimeSlot(timeentity.getUuidTime(), timeentity);
    }

    //-----------------------------
//    this controller of will be used to fetch both the time slot and item of a host
//-----------------------------
    @GetMapping("/guest/host/itemSlot")
    public List<ItemEntity> getItemsTimeSlot(@RequestParam String ids) {
        String[] idSplit = ids.split("#");
        item.getItems("item#" + idSplit[1]);
        timeSlot.getTimeSlot("time#" + idSplit[1]);
        return null;
    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Order controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/guest/order")
    public OrderEntity createOrder(@RequestBody OrderEntity orderEntity) {
        return order.createOrder(orderEntity);
    }

    //need to make changes in the bwloe controller to get orders based on status, i.e in-progress, completed
//host/orders?status=val
//instead of sending orderEntity in body send the hostUuid or guestUUid as that would keep the logic in the backend

    @GetMapping("/host/orders")
    public List<OrderEntity> getHostOrders(@RequestHeader String hostID) {
        return order.getHostOrders(hostID);
    }

    @GetMapping("/host/ipOrders")
    public List<OrderEntity> getInProgressHostOrders(@RequestHeader String hostID) {
        return order.getInProgressHostOrders(hostID);
    }

    @GetMapping("/guest/orders")
    public List<OrderEntity> getGuestOrders(@RequestHeader String uuidOrder) {
        return order.getGuestOrders(uuidOrder);
    }

    @PutMapping("/guest/order")
    public ResponseEntity<String> updateOrder(@RequestBody OrderEntity orderEntity, @RequestParam String attributeName) {
        try {
            // Attempt to update the orderEntity
            OrderEntity updatedOrder = order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName, orderEntity);
             System.out.println(orderEntity);
            if (updatedOrder != null) {
                System.out.println(ResponseEntity.ok("Order updated successfully"));
                // Successfully updated the order
                return ResponseEntity.ok("Order updated successfully");
            } else {
                // Order update failed (handle this case as needed)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order");
            }
        } catch (Exception e) {
            // Handle exceptions (e.g., validation errors, database errors)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }

    @PutMapping("/host/payment")
    public void updatePayment(@RequestBody OrderEntity orderEntity) {
//        return order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), "pyMd", orderEntity);
        order.updatePayment(orderEntity);
    }

    //    @PostMapping("/guest/order/rating")
//    public OrderEntity updateOrderRating(){
//    }
//    ------------------------------------------------------------------------------------------------------
//    **************************************Otp controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/generateOtp")
    public ResponseEntity<String> generateOtp(@RequestBody Map<String, String> payload) {
        String input = payload.get("phone") != null ? payload.get("phone") : payload.get("email");

        try {
            userDetailsService.loadUserByUsername(input);
            String errorMessage = "User already exists for phone: " + input;
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            generatedOtp = PinpointClass.generateOTPWithExpiration();
            otpExpirationTime = LocalDateTime.now().plusSeconds(PinpointClass.getOtpExpirationSeconds());
            return ResponseEntity.ok("OTP generated successfully: " + generatedOtp);
        }
    }

    @PostMapping("/host/otpPhone")
    public ResponseEntity<?> verifySms(@RequestBody OtpVerificationClass otpVerificationClass) {
        return verifyOtp(otpVerificationClass.getPhoneOtp());
    }

    @PostMapping("/host/otpEmail")
    public ResponseEntity<?> verifyEmail(@RequestBody OtpVerificationClass otpVerificationClass) {
        return verifyOtp(otpVerificationClass.getEmailOtp());
    }
//    @PostMapping("/host/otpMob")
//    public ResponseEntity<?> verifySmsGuest(@RequestBody OtpVerificationClass otpVerificationClass) {
//        return verifyOtp(otpVerificationClass.getPhoneOtp());
//    }

    //    @PostMapping("/host/otpEmail")
//    public ResponseEntity<?> verifyEmailGuest(@RequestBody OtpVerificationClass otpVerificationClass) {
//        return verifyOtp(otpVerificationClass.getEmailOtp());
//    }
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
//    -----------------------------------------
//Contstant Charges
//    -----------------------------------------
@PostMapping("/admin/addCharges")
public ResponseEntity<String> addCharges(@RequestBody ConstantChargesEntity constantChargesEntity) {
    return constantCharges.addCharges(constantChargesEntity);
}

    @PutMapping("/admin/updateCharges")
    public ResponseEntity<String> updateCharges(@RequestBody ConstantChargesEntity constantChargesEntity) {
        return constantCharges.updateCharges(constantChargesEntity);
    }

    @GetMapping("/admin/getCharges")
    public ResponseEntity<ConstantChargesEntity> getCharges() {
        return constantCharges.getCharges();
    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************HostAccount controllers******************************************
//    ------------------------------------------------------------------------------------------------------

    @PostMapping("/hostSignup")
    public ResponseEntity<?> getUser(@RequestBody AccountEntity hostEntity) {
        try {
            userDetailsService.loadUserByUsername(hostEntity.getPhone());
            String errorMessage = "User already exists for phone: " + hostEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            // Check if email already exists
            AccountEntity existingUser = account.findUserByEmail(hostEntity.getEmail());
            if (existingUser != null) {
                String errorMessage = "User already exists for email: " + hostEntity.getEmail();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            }

            // User doesn't exist, proceed with saving the details
            AccountEntity newAccountEntity = account.saveSignup(hostEntity, "host");

            // Generate the JWT token for the new user
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

    //    ------------------------------------------------------------------------------------------------------
    //    **************************************GuestAccount controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/guestLogin")
    public ResponseEntity<?> guestLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getPhone());
                String x = account.storeGuestUuid();
                String timestamp = account.storeTimestamp();
                GuestEntity guestDetails=guest.getGuestUsingPk(x);
                Map<String, Object> response = new HashMap<>();
                response.put("uuidGuest", x );
                response.put("token", token);
                response.put("timeStamp", timestamp);
                response.put("guestDetails", guestDetails);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

    @PostMapping("/guestSignup")
    public ResponseEntity<?> getUserGuest(@RequestBody AccountEntity guestEntity) {
        try {
            userDetailsService.loadUserByUsername(guestEntity.getPhone());

            String errorMessage = "User already exists for phone: " + guestEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {

            // User doesn't exist, proceed with saving the details
            AccountEntity newAccountEntity = account.saveSignup(guestEntity, "guest");

            // Generate the JWT token for the new user
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

    //    ------------------------------------------------------------------------------------------------------
    //    **************************************DevBoyAccount controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/devBoySignup")
    public ResponseEntity<?> getUserDev(@RequestBody AccountEntity devBoyEntity) {
        try {
            userDetailsService.loadUserByUsername(devBoyEntity.getPhone());

            String errorMessage = "User already exists for phone: " + devBoyEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {

            // User doesn't exist, proceed with saving the details
            AccountEntity newAccountEntity = account.saveSignup(devBoyEntity, "devBoy");

            // Generate the JWT token for the new user
            String token = jwtServices.generateToken(devBoyEntity.getPhone());
            String uuidGuest = account.storeDevBoyUuid();
            String timestamp = account.storeTimestamp();
            Map<String, Object> response = new HashMap<>();
            response.put("uuidHost", uuidGuest);
            response.put("token", token);
            response.put("timeStamp", timestamp);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/devBoyLogin")
    public ResponseEntity<?> devBoyLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
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

    @GetMapping("/host/ipDevBoyOrders")
    public List<OrderEntity> ipDevBoyOrders(@RequestHeader String uuidDevBoy) {
        return order.getInProgressDevBoyOrders(uuidDevBoy);
    }

    //    -----------------------------------------
//change password
//    -----------------------------------------
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
            // Verify old password
            boolean isPasswordCorrect = account.isPasswordCorrect(passwordChangeRequest.getPhone(),
                    passwordChangeRequest.getOldPassword());

            if (!isPasswordCorrect) {
                String errorMessage = "Incorrect original password.";
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createResponse(false, errorMessage));
            }

            // Save the new password
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
//    -------------------------------------------