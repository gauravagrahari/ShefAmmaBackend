package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeEntity {

    @DynamoDBHashKey(attributeName = "pk")
    private String fName="Heloo";//h_id")
    private String lName="Heloo";//h_id

}
