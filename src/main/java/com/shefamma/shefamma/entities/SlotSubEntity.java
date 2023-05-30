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
    @DynamoDBAttribute(attributeName = "stTm")
    private int startTIme;

    @DynamoDBAttribute(attributeName = "cap")
    private int capacity;
}
