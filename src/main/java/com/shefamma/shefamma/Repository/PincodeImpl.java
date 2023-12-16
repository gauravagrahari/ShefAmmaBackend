package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.shefamma.shefamma.entities.PincodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PincodeImpl implements Pincode{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public PincodeEntity addPincode(PincodeEntity pincodeEntity) {
        dynamoDBMapper.save(pincodeEntity);
        return pincodeEntity;
    }
    @Override
    public ResponseEntity<String> removePincode(String pincode) {
        return null;
    }
    @Override
    public boolean checkPincodeAvailability(String pincode) {
        PincodeEntity pincodeItem = dynamoDBMapper.load(PincodeEntity.class, "pin", pincode);
        return pincodeItem != null && pincodeItem.isStatus();
    }
    @Override
    public ResponseEntity<String> deactivatePincode(String pincode) {
        PincodeEntity pincodeItem = dynamoDBMapper.load(PincodeEntity.class, "pin", pincode);
        if(pincodeItem != null) {
            pincodeItem.setStatus(false);
            dynamoDBMapper.save(pincodeItem);
            return ResponseEntity.ok("Pincode deactivated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pincode not found.");
        }
    }
    @Override
    public List<PincodeEntity> getAllPincodes() {
        PincodeEntity hashKeyValues = new PincodeEntity();
        hashKeyValues.setPk("pin");
        DynamoDBQueryExpression<PincodeEntity> queryExpression = new DynamoDBQueryExpression<PincodeEntity>()
                .withHashKeyValues(hashKeyValues);

        return dynamoDBMapper.query(PincodeEntity.class, queryExpression);
    }
    public ResponseEntity<String> updatePincodeStatus(PincodeEntity pincodeEntity) {
        PincodeEntity existingPincode = dynamoDBMapper.load(PincodeEntity.class, "pin", pincodeEntity.getPincode());
        if (existingPincode != null) {
            existingPincode.setStatus(pincodeEntity.isStatus());
            dynamoDBMapper.save(existingPincode);
            return ResponseEntity.ok("Pincode status updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pincode not found.");
        }
    }

}
