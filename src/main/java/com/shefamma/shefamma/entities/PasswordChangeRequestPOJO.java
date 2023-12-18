package com.shefamma.shefamma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequestPOJO {

    private String phone;
    private String timeStamp;
    private String oldPassword;
    private String newPassword;

    

}
