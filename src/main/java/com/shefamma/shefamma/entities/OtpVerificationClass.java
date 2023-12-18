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
