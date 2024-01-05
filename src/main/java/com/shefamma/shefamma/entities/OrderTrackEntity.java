package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class OrderTrackEntity {

    @DynamoDBHashKey(attributeName = "pk")
    private String pk;

    @DynamoDBRangeKey(attributeName = "sk")
    private String sk;

    @DynamoDBAttribute(attributeName = "hostOrders")
    @DynamoDBTypeConverted(converter = HostOrdersConverter.class)
    private Map<String, Integer> hostOrders; // Map of hostId and corresponding noOfOrders

    @DynamoDBAttribute(attributeName = "mealType")
    private String mealType;

    // Custom converter for the hostOrders map
    public static class HostOrdersConverter implements DynamoDBTypeConverter<Map<String, AttributeValue>, Map<String, Integer>> {
        @Override
        public Map<String, AttributeValue> convert(Map<String, Integer> object) {
            Map<String, AttributeValue> convertedMap = new HashMap<>();
            object.forEach((key, value) -> convertedMap.put(key, new AttributeValue().withN(value.toString())));
            return convertedMap;
        }

        @Override
        public Map<String, Integer> unconvert(Map<String, AttributeValue> object) {
            Map<String, Integer> unconvertedMap = new HashMap<>();
            object.forEach((key, value) -> unconvertedMap.put(key, Integer.parseInt(value.getN())));
            return unconvertedMap;
        }
    }
}
