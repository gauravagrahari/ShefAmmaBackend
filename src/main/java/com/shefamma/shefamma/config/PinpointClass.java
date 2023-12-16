package com.shefamma.shefamma.config;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PinpointClass {
    private static final String OTP_CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final String CHARSET = "UTF-8";
    private static final String HTML_BODY = "<h1>Amazon Pinpoint test (AWS SDK for Java 2.x)</h1>"
            + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
            + "Amazon Pinpoint</a> Email API using the "
            + "<a href='https://aws.amazon.com/sdk-for-java/'>AWS SDK for Java 2.x</a>";

    private static final int OTP_EXPIRATION_SECONDS = 160;

    public static String generateOTPWithExpiration() {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < OTP_LENGTH; i++) {
            int randomIndex = random.nextInt(OTP_CHARACTERS.length());
            char randomChar = OTP_CHARACTERS.charAt(randomIndex);
            otp.append(randomChar);
        }

        LocalDateTime createdTime = LocalDateTime.now();
        LocalDateTime expirationTime = createdTime.plusSeconds(OTP_EXPIRATION_SECONDS);

        System.out.println("Created Time: " + createdTime);
        System.out.println("Expiration Time: " + expirationTime);

        if (isOTPExpired(createdTime)) {
            System.out.println("OTP has expired.");
        } else {
            System.out.println("OTP is valid.");
        }
        System.out.println(otp);
        return otp.toString();
    }

    public static boolean isOTPExpired(LocalDateTime createdTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        return createdTime.plusSeconds(OTP_EXPIRATION_SECONDS).isBefore(currentTime);
    }
    public static int getOtpExpirationSeconds() {
        return OTP_EXPIRATION_SECONDS;
    }}
