package com.shefamma.shefamma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationClass {
    String phone;
    String phoneOtp;
    String email;
    String emailOtp;
}
//{
//  "phone": "1234567890",
//  "phoneOtp": "1234",
//  "email": "example@example.com",
//  "emailOtp": "5678"
//}