package com.shefamma.shefamma.controller;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Geo{

//    @Autowired
//    private GeoApiContext geoApiContext;
//
//    @Autowired
//    private AmazonDynamoDB amazonDynamoDB;
//
//    @GetMapping("/{address}/{radius}")
//    public PlacesSearchResponse findPlacesNearby(@PathVariable String address, @PathVariable int radius) throws InterruptedException, ApiException, IOException {
//        GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();
//        if (results.length > 0) {
//            LatLng guestLocation = results[0].geometry.location;
//            double lat = guestLocation.lat;
//            double lng = guestLocation.lng;
//
//            // Perform a query on the DynamoDB table to retrieve HostEntities within the given radius of the guest location
//            Table table = new DynamoDB(amazonDynamoDB).getTable("HostEntity");
//            String hashKeyName = "location";
//            String hashKeyValue = lat + "," + lng;
//            double radiusInMeters = radius * 1609.34; // convert radius from miles to meters
//            QuerySpec spec = new QuerySpec()
//                    .withKeyConditionExpression("location = :v_location")
//                    .withValueMap((Map<String, Object>) new HashMap<String, Object>()
//                            .put(":v_location", hashKeyValue))
//                    .withFilterExpression("distance(location, :v_guest_location) <= :v_radius")
//                    .withValueMap(new HashMap<String, Object>()
//                            .put(":v_guest_location", guestLocation)
//                            .put(":v_radius", radiusInMeters));
//            ItemCollection<QueryOutcome> items = table.query(spec);
//
//            // Build a list of LatLng objects representing the host locations
//            Map<String, LatLng> hostLocations = new HashMap<>();
//            for (Item item : items) {
//                String hostId = item.getString("id");
//                String[] latLngString = item.getString("location").split(",");
//                double hostLat = Double.parseDouble(latLngString[0]);
//                double hostLng = Double.parseDouble(latLngString[1]);
//                LatLng hostLocation = new LatLng(hostLat, hostLng);
//                hostLocations.put(hostId, hostLocation);
//            }
//
//            // Use the Places API to search for places near the guest location, using the host locations as reference points
//            PlacesSearchResponse response = PlacesApi.nearbySearchQuery(geoApiContext, guestLocation)
//                    .radius(radius)
//                    .keyword("restaurant") // or any other search term
//                    .rankby(RankBy.DISTANCE)
//                    .location(LocationBias.point(hostLocations.values().toArray(new LatLng[0])))
//                    .await();
//
//            return response;
//        } else {
//            return null;
//        }
//    }
}

