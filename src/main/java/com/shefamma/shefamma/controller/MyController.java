package com.shefamma.shefamma.controller;

import com.shefamma.shefamma.HostRepository.*;
import com.shefamma.shefamma.HostRepository.GuestAccount;
import com.shefamma.shefamma.HostRepository.HostAccount;
import com.shefamma.shefamma.entities.*;
import com.shefamma.shefamma.services.JwtServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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
    private UserDetailsService userDetailsService;
    @Autowired
    private HostAccountEntity hostAccountEntity;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtServices jwtServices;

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Host controllers******************************************
//    ------------------------------------------------------------------------------------------------------
//    @PreAuthorize("isAuthenticated()")

    @PostMapping("/host")
    public HostEntity saveHost(@RequestBody HostEntity hostentity) {
        return host.saveHost(hostentity);
    }
    //    @PatchMapping("/host")
//    public HostEntity updateHost( @RequestBody HostEntity hostEntity) {
//       return host.updateHostAttribute("city", hostEntity);
//    }
//    @GetMapping("/guest/hosts")
//    public HostEntity getHost()
    @GetMapping("/guest/host")
    public HostEntity getHost(@RequestBody HostEntity hostentity) {
        return host.getHost(hostentity.getUuidHost(), hostentity.getNameHost());
    }

    @GetMapping("/guest/hosts")
    public HostEntity getHosts(@RequestBody HostEntity hostentity) {
        return host.getHost(hostentity.getUuidHost(), hostentity.getNameHost());
    }

    @PutMapping("/host")
    public HostEntity updateHost(@RequestBody HostEntity hostentity) {
        return host.update(hostentity.getUuidHost(), hostentity.getNameHost(), hostentity);
    }


    //  /guest/host?item=val
    @GetMapping("/guest/host/itemFilter")
    public List<HostEntity> getHostsItemSearchFilter(@RequestParam("item") String itemValue) {
        return host.getHostsItemSearchFilter(itemValue);
    }

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
    public GuestEntity saveGuest(@RequestBody GuestEntity guestentity) {
        return guest.saveGuest(guestentity);
    }

    @GetMapping("/host/guest")
    public GuestEntity getGuest(@RequestBody GuestEntity guestentity) {
        return guest.getGuest(guestentity.getUuidGuest(), guestentity.getGeocode());
    }

    @PutMapping("/guest")
    public GuestEntity updateGuest(@RequestBody GuestEntity guestentity) {
        return guest.updateGuest(guestentity.getUuidGuest(), guestentity.getGeocode(), guestentity);
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
    @PostMapping("/host/time-slot")
    public TimeSlotEntity createTimeSlot(@RequestBody TimeSlotEntity timeentity) {
        return timeSlot.saveSlotTime(timeentity);
    }

    @GetMapping("/guest/host/time-slot")
    public TimeSlotEntity getTimeSlot(@RequestBody TimeSlotEntity timeentity) {
        return timeSlot.getTimeSlot(timeentity.getUuidTime(), timeentity);
    }

    @PutMapping("/host/time-slot")
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
    public OrderEntity cancelOrder(@RequestBody OrderEntity orderEntity) {
        return order.cancelOrder(orderEntity);
    }


   //    ------------------------------------------------------------------------------------------------------
//    **************************************HostAccount controllers******************************************
//    ------------------------------------------------------------------------------------------------------
    @PostMapping("/hostSignup")
    public ResponseEntity<String> getUser(@RequestBody HostAccountEntity hostentity) {
        try {
            userDetailsService.loadUserByUsername(hostentity.getHostPhone());

            String errorMessage = "User already exists for phone: " + hostentity.getHostPhone();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
        } catch (UsernameNotFoundException e) {
            // User doesn't exist, proceed with saving the details
//            String savedHost = hostAccount.saveHostSignup(hostentity);
            String x =hostAccount.saveHostSignup(hostentity);
            return ResponseEntity.ok(x);
        }
    }

    @PostMapping("/hostLogin")
    public ResponseEntity<?> hostLogin(@RequestBody HostAccountEntity authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getHostPhone(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtServices.generateToken(authRequest.getHostPhone());
                String x=hostAccount.storeHostUuid();
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
//    @PostMapping("/hostLogin")
//    public ResponseEntity<?> login(@RequestBody HostAccountEntity hostentity) {
//        try {
//            // Perform authentication
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                    hostentity.getHostPhone(), hostentity.getPassword());
//            Authentication authentication = authenticationManager.authenticate(authenticationToken);
//            // Set authenticated authentication in SecurityContextHolder
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(hostentity.getHostPhone());
//
//            String x=hostAccount.storeHostUuid();
//            return ResponseEntity.ok(x);
//        } catch (AuthenticationException e) {
//            // Authentication failed, return error response
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
//        }
//    }
//
//    //    ------------------------------------------------------------------------------------------------------
////    **************************************GuestAccount controllers******************************************
////    ------------------------------------------------------------------------------------------------------
//    @PostMapping("/guestSignup")
//    public ResponseEntity<GuestAccountEntity>  saveGuestSignup(@RequestBody GuestAccountEntity guestentity) {
//        return guestAccount.saveGuestSignup(guestentity);
//    }
//
//    @PostMapping("/guestLogin")
//    public ResponseEntity<GuestAccountEntity> getGuestLogin(@RequestBody GuestEntity guestentity) {
//        return guestAccount.getGuestLogin(guestentity);
//    }

}
