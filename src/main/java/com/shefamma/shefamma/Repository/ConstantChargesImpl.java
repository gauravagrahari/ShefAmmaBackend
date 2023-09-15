package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.shefamma.shefamma.Repository.ConstantCharges;
import com.shefamma.shefamma.entities.ConstantChargesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ConstantChargesImpl implements ConstantCharges {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public ResponseEntity<String> addCharges(ConstantChargesEntity constantCharges) {
        try {
            dynamoDBMapper.save(constantCharges);
            return ResponseEntity.status(HttpStatus.OK).body("Charges added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add charges");
        }
    }

    @Override
    public ResponseEntity<String> updateCharges(ConstantChargesEntity constantCharges) {
        try {
            dynamoDBMapper.save(constantCharges);
            return ResponseEntity.status(HttpStatus.OK).body("Charges updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update charges");
        }
    }

    @Override
    public ResponseEntity<ConstantChargesEntity> getCharges() {
        try {
            ConstantChargesEntity charges = dynamoDBMapper.load(ConstantChargesEntity.class, "constantCharges", "default");
            if (charges != null) {
                return ResponseEntity.status(HttpStatus.OK).body(charges);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
