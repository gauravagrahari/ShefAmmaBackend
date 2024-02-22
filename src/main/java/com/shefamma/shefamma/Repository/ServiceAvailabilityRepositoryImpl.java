package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.shefamma.shefamma.entities.ServiceAvailability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceAvailabilityRepositoryImpl implements ServiceAvailabilityRepository{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public ServiceAvailability getServiceMessage() {
        return dynamoDBMapper.load(ServiceAvailability.class, "serviceDetails", "default");
    }

    @Override
    public void updateServiceMessage(String message) {
        ServiceAvailability serviceAvailability = new ServiceAvailability();
        serviceAvailability.setServiceDetails("serviceDetails");
        serviceAvailability.setSk("default");
        serviceAvailability.setServiceMessage(message);
        dynamoDBMapper.save(serviceAvailability);
    }
}
