package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.shefamma.shefamma.entities.CapacityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Repository
public class CapacityImpl implements Capacity {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CommonMethods commonMethods;
    @Override
    public CapacityEntity createCapacity(CapacityEntity capacityentity) {
      dynamoDBMapper.save(capacityentity);
      return capacityentity;
    }

    @Override
    public CapacityEntity getCapacity(String id) {
        CapacityEntity hashKeyValues = new CapacityEntity();
        hashKeyValues.setUuidCapacity(id);

        DynamoDBQueryExpression<CapacityEntity> queryExpression = new DynamoDBQueryExpression<CapacityEntity>()
                .withHashKeyValues(hashKeyValues);

        List<CapacityEntity> result = dynamoDBMapper.query(CapacityEntity.class, queryExpression);

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String getCurrentCapacity(String mealType,   String partition) {


        // Mapping meal types to their respective attribute names and retrieval methods
        Map<String, String> attributeMap = new HashMap<>() {{
            put("b", "curBCap");
            put("l", "curLCap");
            put("d", "curDCap");
        }};

        Map<String, Function<CapacityEntity, String>> valueExtractors = new HashMap<>() {{
            put("b", CapacityEntity::getCurrentBreakfastCapacity);
            put("l", CapacityEntity::getCurrentLunchCapacity);
            put("d", CapacityEntity::getCurrentDinnerCapacity);
        }};

        String attributeName = attributeMap.get(mealType);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":partition", new AttributeValue().withS(partition));

        DynamoDBQueryExpression<CapacityEntity> queryExpression = new DynamoDBQueryExpression<CapacityEntity>()
                .withKeyConditionExpression("pk = :partition")
                .withExpressionAttributeValues(eav)
                .withProjectionExpression(attributeName)
                .withConsistentRead(false); // Use eventually consistent read for reduced RCU cost

        List<CapacityEntity> results = dynamoDBMapper.query(CapacityEntity.class, queryExpression);

        if (results != null && !results.isEmpty()) {
            CapacityEntity cap = results.get(0);
            String valueDB = valueExtractors.get(mealType).apply(cap);
            System.out.println(valueDB);
            return valueDB;
        } else {
            // Handle the case where no matching item was found...
            return null;
        }
    }

    private ResponseEntity<String> modifyCapacity(String mealType, String partition, int noOfMeals, boolean increase) {
        String valueDb = getCurrentCapacity(mealType, partition);
        int currentCapacity = Integer.parseInt(valueDb);

        Map<String, String> attributeMap = new HashMap<>() {{
            put("b", "curBCap");
            put("l", "curLCap");
            put("d", "curDCap");
        }};

        String attributeName = attributeMap.get(mealType);
        int updatedValue = increase ? currentCapacity + noOfMeals : currentCapacity - noOfMeals;

        if ((increase && updatedValue >= currentCapacity) || (!increase && noOfMeals <= currentCapacity)) {
            try {
                UpdateItemResult result = commonMethods.updateAttributeWithSortKey(
                        partition,
                        "capacity",
                        attributeName,
                        String.valueOf(updatedValue)
                );
                return ResponseEntity.ok("Capacity updated successfully.");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update capacity: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid capacity modification request.");
        }
    }

    @Override
    public ResponseEntity<String> updateCapacity(String mealType, String partition, int noOfMeals) {
        return modifyCapacity(mealType, partition, noOfMeals, false);
    }

    @Override
    public ResponseEntity<String> increaseCapacity(String mealType, String partition, int noOfMeals) {
        return modifyCapacity(mealType, partition, noOfMeals, true);
    }

    @Override
    public ResponseEntity<String> updateFixedCapacity(String partition, String mealType, String newFixedCapacityStr) {
        int newFixedCapacity = Integer.parseInt(newFixedCapacityStr);

        CapacityEntity currentCapacityEntity = dynamoDBMapper.load(CapacityEntity.class, "capacity#" + partition, "capacity");
        if (currentCapacityEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Capacity entity not found.");
        }

        int currentFixedCapacity;
        int currentCap;

        switch (mealType) {
            case "b" -> {
                currentFixedCapacity = Integer.parseInt(currentCapacityEntity.getBreakfastCapacity());
                currentCap = Integer.parseInt(currentCapacityEntity.getCurrentBreakfastCapacity());
            }
            case "l" -> {
                currentFixedCapacity = Integer.parseInt(currentCapacityEntity.getLunchCapacity());
                currentCap = Integer.parseInt(currentCapacityEntity.getCurrentLunchCapacity());
            }
            case "d" -> {
                currentFixedCapacity = Integer.parseInt(currentCapacityEntity.getDinnerCapacity());
                currentCap = Integer.parseInt(currentCapacityEntity.getCurrentDinnerCapacity());
            }
            default -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid meal type provided.");
            }
        }

        int diff = newFixedCapacity - currentFixedCapacity;

        // Prevent reducing fixed capacity if it results in a negative current capacity
        if (currentCap + diff < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Capacity can't be reduced to the given value without affecting current orders.");
        }

        // Update the fixed and current capacities accordingly
        switch (mealType) {
            case "b" -> {
                currentCapacityEntity.setBreakfastCapacity(Integer.toString(newFixedCapacity));
                currentCapacityEntity.setCurrentBreakfastCapacity(Integer.toString(currentCap + diff));
            }
            case "l" -> {
                currentCapacityEntity.setLunchCapacity(Integer.toString(newFixedCapacity));
                currentCapacityEntity.setCurrentLunchCapacity(Integer.toString(currentCap + diff));
            }
            case "d" -> {
                currentCapacityEntity.setDinnerCapacity(Integer.toString(newFixedCapacity));
                currentCapacityEntity.setCurrentDinnerCapacity(Integer.toString(currentCap + diff));
            }
        }

        dynamoDBMapper.save(currentCapacityEntity); // Save the updated entity

        return ResponseEntity.ok("Capacity updated successfully.");
    }


}
