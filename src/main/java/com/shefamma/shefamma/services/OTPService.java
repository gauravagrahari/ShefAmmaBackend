package com.shefamma.shefamma.services;
import com.shefamma.shefamma.config.PinpointClass;
import com.shefamma.shefamma.controller.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    private final ConcurrentHashMap<String, OtpDetails> otpMap = new ConcurrentHashMap<>();

    @Autowired
    private SmsService smsService;

    @Autowired
    private PinpointClass pinpointClass;

    public void generateAndSendOtp(String identifier) {
        String otp = PinpointClass.generateOTP();
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(PinpointClass.getOtpExpirationSeconds());
        otpMap.put(identifier, new OtpDetails(otp, expirationTime));

        if (identifier.contains("@")) {
            String subject = "Your OTP Code";
            String senderAddress = "noreply@yourdomain.com";
            // Assuming sendEmail method properly implemented in PinpointClass
            pinpointClass.sendEmail(subject, senderAddress, identifier, otp);
        } else {
            smsService.sendOtpSms(identifier, otp);
        }
    }

    public boolean verifyOtp(String identifier, String userOtp) {
        OtpDetails details = otpMap.get(identifier);
        if (details == null || details.isExpired()) {
            otpMap.remove(identifier);
            return false;
        }
        boolean isValid = details.getOtp().equals(userOtp);
        if (isValid) {
            otpMap.remove(identifier);
        }
        return isValid;
    }

    public class OtpDetails {
        private String otp;
        private LocalDateTime expirationTime;

        public OtpDetails(String otp, LocalDateTime expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }
    }
}
