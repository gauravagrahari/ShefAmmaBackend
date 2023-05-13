package com.shefamma.shefamma.controller;

import com.google.maps.model.GeocodingResult;
import com.shefamma.shefamma.HostRepository.*;
import com.shefamma.shefamma.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

//    ------------------------------------------------------------------------------------------------------
//    **************************************Geocode controllers******************************************
//    ------------------------------------------------------------------------------------------------------

//    @Autowired
//    private GeocodingService geocodingService;
//
//    @GetMapping("/{address}")
//    public GeocodingResult[] geocode(@PathVariable String address) throws Exception {
//        return geocodingService.geocode(address);
//    }

    //    ------------------------------------------------------------------------------------------------------
//    **************************************Host controllers******************************************
//    ------------------------------------------------------------------------------------------------------
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
    public List<HostEntity> getHostsTimeSlotSearchFilter(@RequestParam String startTime,@RequestParam String endTime,@RequestParam String timeDuration) {
        return host.getHostsTimeSlotSearchFilter(Integer.parseInt(startTime),Integer.parseInt(endTime), timeDuration);
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

}
