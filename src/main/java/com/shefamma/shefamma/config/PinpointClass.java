package com.shefamma.shefamma.config;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import java.util.HashMap;
import java.util.Map;

@Service
public class PinpointClass {
    private static final String OTP_CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final String CHARSET = "UTF-8";
    private static final String HTML_BODY = "<h1>Amazon Pinpoint test (AWS SDK for Java 2.x)</h1>"
            + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
            + "Amazon Pinpoint</a> Email API using the "
            + "<a href='https://aws.amazon.com/sdk-for-java/'>AWS SDK for Java 2.x</a>";
    public static String appId = "da84a0d8ff3445fb9ba714de83df2c8e";

    private static final int OTP_EXPIRATION_SECONDS = 45;
    public static void sendEmail(String subject,String senderAddress, String toAddress) {
        AwsCredentials credentials = AwsBasicCredentials.create("AKIATDGNA7XLUHRU2K4O", "5FKad7I/6hCmBreMZ/SCZqyL1zJTWu2rJkdaby3");

        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.AP_SOUTH_1)
//                .region(Region.of("ap-south-1"))
                .credentialsProvider(() -> credentials)
                .build();

        try {
            Map<String, AddressConfiguration> addressMap = new HashMap<>();
            AddressConfiguration configuration = AddressConfiguration.builder()
                    .channelType(ChannelType.EMAIL)
                    .build();

            addressMap.put(toAddress, configuration);

            SimpleEmailPart emailPart = SimpleEmailPart.builder()
                    .data(HTML_BODY)
                    .charset(CHARSET)
                    .build();

            SimpleEmailPart subjectPart = SimpleEmailPart.builder()
                    .data(subject)
                    .charset(CHARSET)
                    .build();

            SimpleEmail simpleEmail = SimpleEmail.builder()
                    .htmlPart(emailPart)
                    .subject(subjectPart)
                    .build();

            EmailMessage emailMessage = EmailMessage.builder()
                    .body(HTML_BODY)
                    .fromAddress(senderAddress)
                    .simpleEmail(simpleEmail)
                    .build();

            DirectMessageConfiguration directMessageConfiguration = DirectMessageConfiguration.builder()
                    .emailMessage(emailMessage)
                    .build();

            MessageRequest messageRequest = MessageRequest.builder()
                    .addresses(addressMap)
                    .messageConfiguration(directMessageConfiguration)
                    .build();

            SendMessagesRequest messagesRequest = SendMessagesRequest.builder()
                    .applicationId(appId)
                    .messageRequest(messageRequest)
                    .build();

            pinpoint.sendMessages(messagesRequest);
            System.out.println("Email sent successfully.");
        } catch (PinpointException e) {
            System.err.println("Error sending email: " + e.awsErrorDetails().errorMessage());
        } finally {
            pinpoint.close();
        }
    }

    public static void sendSMS(String message, String originationNumber, String destinationNumber) {
        AwsCredentials credentials = AwsBasicCredentials.create("AKIATDGNA7XLUHRU2K4O", "5FKad7I/6hCmBreMZ/SCZqyL1zJTWu2rJkdaby3");

        PinpointClient pinpoint = PinpointClient.builder()
//                .region(Region.AP_SOUTH_1)
                .region(Region.of("ap-south-1"))
                .credentialsProvider(() -> credentials)
                .build();

        try {
            Map<String, AddressConfiguration> addressMap = new HashMap<>();
            AddressConfiguration addConfig = AddressConfiguration.builder()
                    .channelType(ChannelType.SMS)
                    .build();

            addressMap.put(destinationNumber, addConfig);

            SMSMessage smsMessage = SMSMessage.builder()
                    .body(message)
                    .messageType("TRANSACTIONAL")
                    .originationNumber(originationNumber)
                    .senderId("MySenderID")
                    .build();

            DirectMessageConfiguration direct = DirectMessageConfiguration.builder()
                    .smsMessage(smsMessage)
                    .build();

            MessageRequest messageRequest = MessageRequest.builder()
                    .addresses(addressMap)
                    .messageConfiguration(direct)
                    .build();

            SendMessagesRequest sendMessagesRequest = SendMessagesRequest.builder()
                    .applicationId(appId)
                    .messageRequest(messageRequest)
                    .build();

            pinpoint.sendMessages(sendMessagesRequest);
            System.out.println("SMS sent successfully.");
        } catch (PinpointException e) {
            System.err.println("Error sending SMS: " + e.awsErrorDetails().errorMessage());
        } finally {
            pinpoint.close();
        }
    }



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
    }

//    The provided code for generating and verifying OTP should work fine in a production environment. However, it's important to consider a few aspects and potential issues to ensure the reliability and security of your OTP system:
//
//    Thread safety: If multiple requests can be processed concurrently, you should ensure that the generatedOtp and otpExpirationTime variables are accessed and modified safely to avoid race conditions. Consider using synchronization mechanisms or thread-safe data structures if necessary.
//
//    Storage and persistence: In the provided code, the generated OTP and its expiration time are stored in memory variables. However, in a production environment, you may need to consider storing this information in a more persistent and scalable manner, such as a database or a distributed caching system, depending on your specific requirements.
//
//    Security considerations: OTPs are commonly used for authentication and security purposes, so it's crucial to implement additional security measures. This includes protecting against brute-force attacks, ensuring secure transmission of OTPs, and using strong encryption for sensitive data.
//
//    System scalability: If your system experiences high traffic or requires horizontal scaling, you need to consider how the OTP generation and verification will work across multiple instances or nodes. Ensure that the OTP generation and storage are designed to handle the scalability requirements of your application.
//
//    Testing and monitoring: It's important to thoroughly test the OTP generation and verification functionality and monitor the system in production to identify any potential issues, such as incorrect OTPs or expired OTPs.
}
