package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
@Component
public class SlotSubEntity {
    @DynamoDBAttribute(attributeName = "stTm")
    private int startTime;
    @DynamoDBAttribute(attributeName = "ccap")
    private int currentCapacity;
}
