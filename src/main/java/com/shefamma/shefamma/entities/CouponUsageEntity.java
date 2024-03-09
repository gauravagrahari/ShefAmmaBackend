package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "ShefAmma")
public class CouponUsageEntity {

    @DynamoDBHashKey(attributeName = "pk")
    private String userId; // pk: "coupon#<UserID>"

    @DynamoDBRangeKey(attributeName = "sk")
    private String couponCode; // sk: "couponCode#<CouponCodeID>"

    @DynamoDBAttribute(attributeName = "uses")
    private int uses;
    
    public CouponUsageEntity setUserId(String userId) {
        if (userId.startsWith("coupon#")) {
            this.userId = userId;
        } else {
            this.userId = "coupon#" + userId;
        }
        return this;
    }
    public CouponUsageEntity setCouponCode(String couponCode) {
        if (userId.startsWith("couponCode#")) {
            this.couponCode = couponCode;
        } else {
            this.couponCode = "couponCode#" + couponCode;
        }
        return this;
    }
}
