package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.shefamma.shefamma.entities.DevBoyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class DevBoyImpl implements DevBoy{

    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CommonMethods commonMethods;
    private Map<String, String> devBoyData;


    public DevBoyEntity update(String partition, String sort, String attributeName, DevBoyEntity dev) {
        String value = null;

        switch (attributeName) {
            case "status" -> {
                value = dev.getStatus();
                attributeName = "stts";
            }
            case "vehicleType" -> {
                value = dev.getVehicleType();
                attributeName = "veh";
            }
            default -> throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        
        UpdateItemResult response = commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
        System.out.println(response);

        try {

            return dev;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Host entity. Error: " + e.getMessage());
        }
}

    public DevBoyEntity saveDevBoy(DevBoyEntity devBoyEntity) {
        
         dynamoDBMapper.save(devBoyEntity);
        return devBoyEntity;
    }
}
