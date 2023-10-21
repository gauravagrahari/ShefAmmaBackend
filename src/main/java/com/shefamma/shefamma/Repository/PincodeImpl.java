package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.shefamma.shefamma.entities.PincodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class PincodeImpl implements Pincode{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public PincodeEntity addPincode(PincodeEntity pincode) {
        dynamoDBMapper.save(pincode);
        return pincode;
    }
    @Override
    public ResponseEntity<String> removePincode(String pincode) {
        return null;
    }

    @Override
    public boolean checkPincodeAvailability(String pincode) {
        if(!pincode.startsWith("pin#")) pincode = "pin#" + pincode;
        PincodeEntity pincodeItem=dynamoDBMapper.load(PincodeEntity.class,pincode);
        return pincodeItem != null;
    }
}
