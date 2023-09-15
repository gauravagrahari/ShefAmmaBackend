package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.ConstantChargesEntity;
import com.shefamma.shefamma.entities.OrderEntity;
import org.springframework.http.ResponseEntity;

public interface ConstantCharges {

    ResponseEntity<String> addCharges(ConstantChargesEntity constantCharges);

    ResponseEntity<String> updateCharges(ConstantChargesEntity constantCharges);

    ResponseEntity<ConstantChargesEntity> getCharges();
}
