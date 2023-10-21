package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.PincodeEntity;
import org.springframework.http.ResponseEntity;

public interface Pincode {
     PincodeEntity addPincode(PincodeEntity pincode);
     ResponseEntity<String> removePincode(String pincode);
     boolean checkPincodeAvailability(String pincode);
}
