package com.shefamma.shefamma.config;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GoogleDirections {

//    private static final String API_KEY = "AIzaSyBgME4kzfk-uOisOH9Z1cUekeIAaf6z-FU";
//    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s";
//
//    public static void main(String[] args) throws Exception {
//        String origin = "40.7128,-74.0060";  // New York
//        String destination = "34.0522,-118.2437";  // Los Angeles
//
//        String urlToRequest = String.format(BASE_URL, origin, destination, API_KEY);
//        HttpURLConnection connection = (HttpURLConnection) new URL(urlToRequest).openConnection();
//        connection.setRequestMethod("GET");
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String line;
//        StringBuilder response = new StringBuilder();
//        while ((line = reader.readLine()) != null) {
//            response.append(line);
//        }
//        reader.close();
//        System.out.println(response.toString());
//    }
}
