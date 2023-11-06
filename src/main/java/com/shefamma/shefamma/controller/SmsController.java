package com.shefamma.shefamma.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Value("${apiBaseUrl}")
    private String API_BASE_URL;
//    https://restapi.smscountry.com/v0.1/Accounts/yIxoqdwxoR7EFu64p66b/SMSes/

    @Value("${authKey}")
    private String authKey;

    @Value("${authToken}")
    private String authToken;

    @PostMapping("/send")
    public ResponseEntity<SmsResponse> sendSms(@RequestBody SmsRequest smsRequest) {
        RestTemplate restTemplate = new RestTemplate();

        String authStr = authKey + ":" + authToken;
        String base64AuthStr = "Basic " + Base64.getEncoder().encodeToString(authStr.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", base64AuthStr);
        System.out.println("encoded  "+base64AuthStr);
        OutgoingSmsRequest outgoingRequest = new OutgoingSmsRequest(smsRequest);
        HttpEntity<OutgoingSmsRequest> request = new HttpEntity<>(outgoingRequest, headers);

        String smsUrl = API_BASE_URL + authKey + "/SMSes/";

        return restTemplate.exchange(smsUrl, HttpMethod.POST, request, SmsResponse.class);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OutgoingSmsRequest {
        @JsonProperty("Text")
        private String text;

        @JsonProperty("Number")
        private String number;

        @JsonProperty("SenderId")
        private String senderId;

        @JsonProperty("DRNotifyUrl")
        private String drnotifyUrl;

        @JsonProperty("DRNotifyHttpMethod")
        private String drnotifyHttpMethod;

        @JsonProperty("Tool")
        private String tool;

        public OutgoingSmsRequest(SmsRequest smsRequest) {
            this.text = smsRequest.getText();
            this.number = smsRequest.getNumber();
            this.senderId = smsRequest.getSenderId();
            this.drnotifyUrl = smsRequest.getDrnotifyUrl();
            this.drnotifyHttpMethod = smsRequest.getDrnotifyHttpMethod();
            this.tool = smsRequest.getTool();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SmsRequest {
        private String text;
        private String number;
        private String senderId;
        private String drnotifyUrl;
        private String drnotifyHttpMethod;
        private String tool;
    }

    @Component
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SmsResponse {
        private String ApiId;
        private boolean Success;
        private String Message;
        private String MessageUUID;

        // Getters, Setters, and a No-Args Constructor
    }
}
