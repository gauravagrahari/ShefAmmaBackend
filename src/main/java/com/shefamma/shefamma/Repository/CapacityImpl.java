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
import java.util.Objects;
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

    @Override
    public ResponseEntity<String> updateCapacity(String mealType,  String partition, int noOfMeals) {
        String valueDb = getCurrentCapacity(mealType, partition);

        if (noOfMeals <= Integer.parseInt(valueDb)) {
            Map<String, String> attributeMap = new HashMap<>() {{
                put("b", "curBCap");
                put("l", "curLCap");
                put("d", "curDCap");
            }};

            String attributeName = attributeMap.get(mealType);
            int updatedValue = Integer.parseInt(valueDb) - noOfMeals;

            try {
                UpdateItemResult result = commonMethods.updateAttributeWithSortKey(
                        partition,
                        "capacity",
                        attributeName,
                        String.valueOf(updatedValue)
                );

                // If you want to make use of 'result', you can do so here
                // For now, we'll simply return a success message
                return ResponseEntity.ok("Capacity updated successfully.");

            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update capacity: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No. of meals exceeds available capacity.");
        }
    }


}
