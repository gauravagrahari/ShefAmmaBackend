package com.shefamma.shefamma.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${apiBaseUrl}")
    private String API_BASE_URL;

    @Value("${authKey}")
    private String authKey;

    @Value("${authToken}")
    private String authToken;
    @Value("${sms.otp.template}")
    private String otpTemplate;

    public ResponseEntity<SmsResponse> sendOtpSms(String number, String otp) {
        RestTemplate restTemplate = new RestTemplate();

        String authStr = authKey + ":" + authToken;
        String base64AuthStr = "Basic " + Base64.getEncoder().encodeToString(authStr.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", base64AuthStr);

        String otpMessage = String.format(otpTemplate, otp);

        OutgoingSmsRequest outgoingRequest = new OutgoingSmsRequest(otpMessage, "91" + number, "SHFAMA", "https://www.domainname.com/notifyurl", "POST", "API");

        HttpEntity<OutgoingSmsRequest> request = new HttpEntity<>(outgoingRequest, headers);

        String smsUrl = API_BASE_URL + authKey + "/SMSes/";

        try {
            ResponseEntity<SmsResponse> response = restTemplate.exchange(smsUrl, HttpMethod.POST, request, SmsResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                SmsResponse smsResponse = response.getBody();
                logger.info("SMS response: {}", smsResponse); // Log the entire response

//                if (smsResponse != null) {
                if (smsResponse != null && smsResponse.isSuccess()) {
                    logger.info("SMS sent successfully: {}", smsResponse.getMessage());
                    return ResponseEntity.ok(smsResponse);
                } else {
                    logger.error("SMS failed with message: {}", smsResponse != null ? smsResponse.getMessage() : "Unknown error");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(smsResponse);
                }
            } else {
                logger.error("Failed to send SMS, HTTP status code: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            logger.error("Exception occurred while sending SMS: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SmsResponse(null, false, "Exception occurred", null));
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OutgoingSmsRequest {
        @JsonProperty("Text")
        private String Text;

        @JsonProperty("Number")
        private String Number;

        @JsonProperty("SenderId")
        private String SenderId;

        @JsonProperty("DRNotifyUrl")
        private String DRNotifyUrl;

        @JsonProperty("DRNotifyHttpMethod")
        private String DRNotifyHttpMethod;

        @JsonProperty("Tool")
        private String Tool;

        public OutgoingSmsRequest(SmsRequest smsRequest) {
            this.Text = smsRequest.getText();
            this.Number = smsRequest.getNumber();
            this.SenderId = smsRequest.getSenderId();
            this.DRNotifyUrl = smsRequest.getDrnotifyUrl();
            this.DRNotifyHttpMethod = smsRequest.getDrnotifyHttpMethod();
            this.Tool = smsRequest.getTool();
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SmsResponse {
        @JsonProperty("ApiId")
        private String apiId; // Ensure the field names match the JSON property names

        @JsonProperty("Success")
        private boolean success; // This should be a boolean as per your API's response

        @JsonProperty("Message")
        private String message;

        @JsonProperty("MessageUUID")
        private String messageUUID;
    }

    public static class ServiceException extends RuntimeException {
        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
