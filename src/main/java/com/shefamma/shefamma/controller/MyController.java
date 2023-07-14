package com.shefamma.shefamma.controller;

import com.google.maps.model.GeocodingResult;
import com.shefamma.shefamma.HostRepository.*;
import com.shefamma.shefamma.HostRepository.GuestAccount;
import com.shefamma.shefamma.HostRepository.Account;
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
    private TimeSlot timeSlot;
    @Autowired
    private Order order;
    @Autowired
    private Account account;
    @Autowired
    private GuestAccount guestAccount;
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

    @PostMapping("/host/generateOtp")
    public ResponseEntity<String> generateOtp() {
        generatedOtp = PinpointClass.generateOTPWithExpiration();
        otpExpirationTime = LocalDateTime.now().plusSeconds(PinpointClass.getOtpExpirationSeconds());
        return ResponseEntity.ok("OTP generated successfully: " + generatedOtp);
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

//    ------------------------------------------------------------------------------------------------------
//    **************************************Host controllers******************************************
//    ------------------------------------------------------------------------------------------------------

    @CrossOrigin(origins = "*")
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
        return host.update(hostentity.getUuidHost(), hostentity.getNameHost(), attributeName, hostentity);
    }

    @GetMapping("/guest/host")
    public HostEntity getHostforGuest(@RequestBody HostEntity hostentity) {
        return host.getHost(hostentity.getGsiPk(), hostentity.getUuidHost());
    }

    @GetMapping("/host")
    public HostEntity getHost(@RequestBody HostEntity hostentity) {
        return host.getHost(hostentity.getGsiPk(), hostentity.getUuidHost());
    }

    ///guest/hosts?radius=val
    @GetMapping("/guest/hosts")
    public List<HostCardEntity> getHostsWithinRadius(@RequestHeader("UUID") String uuidGuest, @RequestParam double radius) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(guest.getGuest(uuidGuest).convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        List<HostCardEntity> x = host.findRestaurantsWithinRadius(latitude, longitude, radius);
        System.out.println(x);
//        return host.findRestaurantsWithinRadius(latitude, longitude, radius);
        return x;
    }

    //  /guest/host?item=val&radius=val
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
public OrderEntity getHostRatingReview(@RequestBody HostEntity hostEntity){
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
        return guest.saveGuest(guestEntity);
    }

    @GetMapping("/host/guest")
    public GuestEntity getGuest(@RequestBody GuestEntity guestentity) {
        return guest.getGuest(guestentity.getUuidGuest(), guestentity.getGeocode());
    }

    ///guest?attributeName=val
    @PutMapping("/guest")
    public GuestEntity updateGuest(@RequestBody GuestEntity guestentity, @RequestParam String attributeName) {
        return guest.updateGuest(guestentity.getUuidGuest(), guestentity.getGeocode(), attributeName, guestentity);
    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Item controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/menuItem")
    public List<ItemEntity> createItems(@NotNull @RequestBody List<ItemEntity> itemEntities) {
        List<ItemEntity> items = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntities) {
            ItemEntity savedItem = item.saveItem(itemEntity);
            items.add(savedItem);
        }
        return items;
    }

    @PutMapping("/host/menuItem")
    public ItemEntity updateItem(@RequestBody ItemEntity itementity) {
        return item.updateItem(itementity.getUuidItem(), itementity.getNameItem(), itementity);
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
//    **************************************TimeSlot controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/timeSlot")
    public TimeSlotEntity createTimeSlot(@RequestBody TimeSlotEntity timeentity) {
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
    public List<OrderEntity> getHostOrders(@RequestBody OrderEntity orderEntity) {
        return order.getHostOrders(orderEntity);
    }

    @GetMapping("/guest/orders")
    public List<OrderEntity> getGuestOrders(@RequestBody OrderEntity orderEntity) {
        return order.getGuestOrders(orderEntity);
    }

    @PutMapping("/guest/order")
    public OrderEntity updateOrder(@RequestBody OrderEntity orderEntity, @RequestParam String attributeName) {
        return order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), attributeName, orderEntity);
    }
    @PutMapping("/host/payment")
    public void updatePayment(@RequestBody OrderEntity orderEntity){
//        return order.updateOrder(orderEntity.getUuidOrder(), orderEntity.getTimeStamp(), "pyMd", orderEntity);
         order.updatePayment(orderEntity);
    }
//    @PostMapping("/guest/order/rating")
//    public OrderEntity updateOrderRating(){
//    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************HostAccount controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/hostSignup")
    public ResponseEntity<?> getUser(@RequestBody AccountEntity hostentity) {
        try {
            userDetailsService.loadUserByUsername(hostentity.getPhone());

            String errorMessage = "User already exists for phone: " + hostentity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            // User doesn't exist, proceed with saving the details
            String x = account.saveSignup(hostentity,"host");

            // Generate the JWT token for the new user
            String token = jwtServices.generateToken(hostentity.getPhone());
            Map<String, Object> response = new HashMap<>();
            response.put("x", x);
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/hostLogin")
    public ResponseEntity<?> hostLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getPhone());
                String x = account.storeHostUuid();
                Map<String, Object> response = new HashMap<>();
                response.put("x", x);
                response.put("token", token);
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
    @PostMapping("/guestSignup")
    public ResponseEntity<?> getUserGuest(@RequestBody AccountEntity guestEntity) {
        try {
            userDetailsService.loadUserByUsername(guestEntity.getPhone());

            String errorMessage = "User already exists for phone: " + guestEntity.getPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            // User doesn't exist, proceed with saving the details
            String x = account.saveSignup(guestEntity,"guest");

            // Generate the JWT token for the new user
            String token = jwtServices.generateToken(guestEntity.getPhone());
            Map<String, Object> response = new HashMap<>();
            response.put("x", x);
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/guestLogin")
    public ResponseEntity<?> guestLogin(@RequestBody AccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getPhone());
                String x = account.storeGuestUuid();
                Map<String, Object> response = new HashMap<>();
                response.put("x", x);
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

//    @PostMapping("/guestSignup")
//    public ResponseEntity<?> getUser(@RequestBody GuestAccountEntity guestEntity) {
//        try {
//            userDetailsServiceGuest.loadUserByUsername(guestEntity.getGuestPhone());
//
//            String errorMessage = "User already exists for phone: " + guestEntity.getGuestPhone();
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
//        } catch (UsernameNotFoundException e) {
//            // User doesn't exist, proceed with saving the details
//            String x = guestAccount.saveGuestSignup(guestEntity);
//            // Generate the JWT token for the new user
//            String token = jwtServices.generateToken(guestEntity.getGuestPhone());
//            Map<String, Object> response = new HashMap<>();
//            response.put("x", x);
//            response.put("token", token);
//            return ResponseEntity.ok(response);
//        }
//    }

//    @PostMapping("/guestLogin")
//    public ResponseEntity<?> guestLogin(@RequestBody GuestAccountEntity authRequest) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getGuestPhone(), authRequest.getPassword()));
//
//            if (authentication.isAuthenticated()) {
//                String token = jwtServices.generateToken(authRequest.getGuestPhone());
//                String x = guestAccount.storeGuestUuid();
//                Map<String, Object> response = new HashMap<>();
//                response.put("x", x);
//                response.put("token", token);
//                return ResponseEntity.ok(response);
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//            }
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
//        }
//    }
}