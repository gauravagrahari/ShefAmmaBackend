package com.shefamma.shefamma.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.GeocodingResult;
import com.shefamma.shefamma.HostRepository.*;
import com.shefamma.shefamma.HostRepository.GuestAccount;
import com.shefamma.shefamma.HostRepository.HostAccount;
import com.shefamma.shefamma.config.GeocodingService;
import com.shefamma.shefamma.entities.*;
import com.shefamma.shefamma.services.JwtServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private HostAccount hostAccount;
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
    private HostAccountEntity hostAccountEntity;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtServices jwtServices;
    @Autowired
    private GeocodingService geocodingService;

    @CrossOrigin(origins = "*")
    @PostMapping("/home")
    public String home(@RequestBody String message) {
        System.out.println(message);
        return homeEntity.getFName();
    }
    //    ------------------------------------------------------------------------------------------------------
//    **************************************Host controllers******************************************
//    ------------------------------------------------------------------------------------------------------

    @PostMapping("/host")
    public HostEntity saveHost(@RequestBody HostEntity hostEntity) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(hostEntity.getAddressHost().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        String coordinates = String.format("%.6f,%.6f", latitude, longitude);
        hostEntity.setGeocode(coordinates);
        return host.saveHost(hostEntity);
    }
    ///host?attribute=val
    @PutMapping("/host")
    public HostEntity updateHost(@RequestBody HostEntity hostentity, @RequestParam String attributeName) {
        return host.update(hostentity.getUuidHost(), hostentity.getNameHost(), attributeName, hostentity);
    }
    @GetMapping("/guest/host")
    public HostEntity getHost(@RequestBody HostEntity hostentity) {
        return host.getHost(hostentity.getGsiPk(), hostentity.getUuidHost());
    }
///guest/hosts?radius=val
    @PostMapping("/guest/hosts")
    public List<HostCardEntity> getHostsWithinRadius(@RequestBody GuestEntity hostEntity, @RequestParam double radius) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(hostEntity.getAddressGuest().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;

        return host.findRestaurantsWithinRadius(latitude, longitude, radius);
    }
    //  /guest/host?item=val&radius=val
    @GetMapping("/guest/hosts/itemSearch")
    public List<HostCardEntity> getHostsItemSearchFilter(@RequestBody GuestEntity guestEntity, @RequestParam("item") String itemValue,@RequestParam double radius) throws Exception {
        GeocodingResult[] results = geocodingService.geocode(guestEntity.getAddressGuest().convertToString());
        double latitude = results[0].geometry.location.lat;
        double longitude = results[0].geometry.location.lng;
        return host.getHostsItemSearchFilter(latitude, longitude, radius,itemValue);
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
//    ------------------------------------------------------------------------------------------------------
//    **************************************Guest controllers******************************************
//    ------------------------------------------------------------------------------------------------------
@PostMapping("/guest")
public GuestEntity saveGuest(
        @RequestPart("uuidGuest") String uuidGuest,
        @RequestPart("geocode") String geocode,
        @RequestPart("name") String name,
        @RequestPart("DP") MultipartFile DP,
        @RequestPart("addressGuest") String addressGuestJson) throws IOException {

    ObjectMapper objectMapper = new ObjectMapper();
    AdressSubEntity addressGuest = objectMapper.readValue(addressGuestJson, AdressSubEntity.class);

    GuestEntity guestEntity = new GuestEntity();
    guestEntity.setUuidGuest(uuidGuest);
    guestEntity.setGeocode(geocode);
    guestEntity.setName(name);
    guestEntity.setDP(DP.getBytes());
    guestEntity.setAddressGuest(addressGuest);

    return guest.saveGuest(guestEntity);
}


    @GetMapping("/host/guest")
    public GuestEntity getGuest(@RequestBody GuestEntity guestentity) {
        return guest.getGuest(guestentity.getUuidGuest(), guestentity.getGeocode());
    }

    @PutMapping("/guest")
    public GuestEntity updateGuest(@RequestBody GuestEntity guestentity, @RequestParam String attributeName) {
        return guest.updateGuest(guestentity.getUuidGuest(), guestentity.getGeocode(), attributeName, guestentity);
    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Item controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/host/menuItem")
    public ItemEntity createItem(@RequestBody ItemEntity itementity) {
        return item.saveItem(itementity);
    }

    @PutMapping("/host/menuItem")
    public ItemEntity updateItem(@RequestBody ItemEntity itementity) {
        return item.updateItem(itementity.getUuidItem(), itementity.getNameItem(), itementity);
    }

    @GetMapping("/guest/host/menuItems")
    public List<ItemEntity> getItems(@RequestBody ItemEntity itementity) {
        return item.getItems(itementity.getUuidItem(), itementity);
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
        return timeSlot.saveSlotTime(timeentity);
    }

    @GetMapping("/guest/host/timeSlot")
    public TimeSlotEntity getTimeSlot(@RequestBody TimeSlotEntity timeentity) {
        return timeSlot.getTimeSlot(timeentity.getUuidTime(), timeentity);
    }

    @PutMapping("/host/timeSlot")
    public TimeSlotEntity updateTimeSlot(@RequestBody TimeSlotEntity timeentity) {
        return timeSlot.updateTimeSlot(timeentity.getUuidTime(), timeentity);
    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Order controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/guest/order")
    public OrderEntity createOrder(@RequestBody OrderEntity orderEntity) {
        return order.createOrder(orderEntity);
    }

    @GetMapping("/host/orders")
    public List<OrderEntity> getHostOrders(@RequestBody OrderEntity orderEntity) {
        return order.getHostOrders(orderEntity);
    }

    @GetMapping("/guest/orders")
    public List<OrderEntity> getGuestOrders(@RequestBody OrderEntity orderEntity) {
        return order.getGuestOrders(orderEntity);
    }

    @PutMapping("/guest/order")
    public OrderEntity cancelOrder(@RequestBody OrderEntity orderEntity, @RequestParam String attributeName) {
        return order.cancelOrder(orderEntity.getUuidOrder(),orderEntity.getTimeStamp(),attributeName,orderEntity);
    }


    //    ------------------------------------------------------------------------------------------------------
//    **************************************HostAccount controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/hostSignup")
    public ResponseEntity<?> getUser(@RequestBody HostAccountEntity hostentity) {
        try {
            userDetailsService.loadUserByUsername(hostentity.getHostPhone());

            String errorMessage = "User already exists for phone: " + hostentity.getHostPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            // User doesn't exist, proceed with saving the details
            String x = hostAccount.saveHostSignup(hostentity);

            // Generate the JWT token for the new user
            String token = jwtServices.generateToken(hostentity.getHostPhone());
            Map<String, Object> response = new HashMap<>();
            response.put("x", x);
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
    }


    @PostMapping("/hostLogin")
    public ResponseEntity<?> hostLogin(@RequestBody HostAccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getHostPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getHostPhone());
                String x = hostAccount.storeHostUuid();
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

    @PostMapping("/guestLogin")
    public ResponseEntity<?> guestLogin(@RequestBody GuestAccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getGuestPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getGuestPhone());
                String x = guestAccount.storeGuestUuid();
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
}