package com.shefamma.shefamma.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.shefamma.shefamma.entities.GuestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
@Service
public class NotificationService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private RestTemplate restTemplate = new RestTemplate();

    // Method to send notifications to all guests
    public void sendNotificationToAllGuests(String title, String body) {
        List<GuestEntity> guests = getAllGuest();
        System.out.println("Total guests retrieved: " + guests.size()); // Print the number of guests retrieved

        for (GuestEntity guest : guests) {
            if (guest.getExpoPushToken() != null && !guest.getExpoPushToken().isEmpty()) {
                // Personalize the title and body for each guest
                String personalizedTitle = personalizeMessage(title, guest.getName());
                String personalizedBody = personalizeMessage(body, guest.getName());
                System.out.println("Sending notification to token: " + guest.getExpoPushToken()); // Print each token
                sendNotification(guest.getExpoPushToken(), personalizedTitle, personalizedBody);
            }
        }
    }

    private void sendNotification(String expoPushToken, String title, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String notificationJson = buildNotificationJson(expoPushToken, title, body);

        HttpEntity<String> request = new HttpEntity<>(notificationJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("https://exp.host/--/api/v2/push/send", request, String.class);

        System.out.println("Notification sent to Expo with response: " + response.getStatusCode() + " " + response.getBody()); // Print response from Expo
    }

    private String buildNotificationJson(String expoPushToken, String title, String body) {
        return String.format("{\"to\":\"%s\",\"title\":\"%s\",\"body\":\"%s\"}", expoPushToken, title, body);
    }

    private String personalizeMessage(String message, String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            String firstName = fullName.split(" ")[0]; // Assumes the first word in the name is the first name
            return message.replace("###", firstName);
        }
        return message;
    }

    // Retrieve all guests from DynamoDB
    private List<GuestEntity> getAllGuest() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("begins_with(pk, :prefix)")
                .withExpressionAttributeValues(Map.of(":prefix", new AttributeValue().withS("guest#")));

        return dynamoDBMapper.scan(GuestEntity.class, scanExpression);
    }
}