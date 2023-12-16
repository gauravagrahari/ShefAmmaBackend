package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.shefamma.shefamma.entities.DevBoyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public class DevBoyImpl implements DevBoy{

    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private CommonMethods commonMethods;
    private Map<String, String> devBoyData;//key is uuidDevBoy, value is geocode


    public DevBoyEntity update(String partition, String sort, String attributeName, DevBoyEntity dev) {
        String value = null;
        // Get the value of the specified attribute
        switch (attributeName) {
            case "status":
                value = dev.getStatus();
                attributeName="stts";
                break;
            case "vehicleType":
                value = dev.getVehicleType();
                attributeName="veh";
                break;
            // Add more cases for other attributes if needed
            default:
                // Invalid attribute name provided
                throw new IllegalArgumentException("Invalid attribute name: " + attributeName);
        }
        // attributeName given to this method should be the attribute corresponding to the name in dynamodb table.
        UpdateItemResult response = commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
        System.out.println(response);

        try {
//            commonMethods.updateAttributeWithSortKey(partition, sort, attributeName, value);
            return dev;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update Host entity. Error: " + e.getMessage());
        }
}

    public DevBoyEntity saveDevBoy(DevBoyEntity devBoyEntity) {
        // Implement the logic to save the DevBoyEntity to DynamoDB
         dynamoDBMapper.save(devBoyEntity);
        return devBoyEntity;
    }
}
