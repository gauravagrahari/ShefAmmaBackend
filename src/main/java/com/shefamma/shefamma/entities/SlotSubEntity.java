package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class SlotSubEntity {
    @DynamoDBAttribute
    private String id;
    @DynamoDBAttribute
    private String startTIme;
    @DynamoDBAttribute
    private int capacity;
}
