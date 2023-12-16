package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.PincodeEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Pincode {
     PincodeEntity addPincode(PincodeEntity pincode);
     ResponseEntity<String> removePincode(String pincode);
     boolean checkPincodeAvailability(String pincode);
     ResponseEntity<String> deactivatePincode(String pincode);

     List<PincodeEntity> getAllPincodes();

     ResponseEntity<String> updatePincodeStatus(PincodeEntity pincodeEntity);
}
