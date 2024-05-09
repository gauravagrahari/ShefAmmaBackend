package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
@Getter
@Setter
public class CouponUsageEntity {

    @DynamoDBHashKey(attributeName = "pk")
    private String userId; // pk: "coupon#<UserID>"

    @DynamoDBRangeKey(attributeName = "sk")
    private String couponCode; // sk: "couponCode#<CouponCodeID>"

    @DynamoDBAttribute(attributeName = "uses")
    private int uses;

    public CouponUsageEntity setUserId(String userId) {
        if (userId.startsWith("guest#")) {
            this.userId = "coupon#" + userId.substring(6);
        } else if (!userId.startsWith("coupon#")) {
            this.userId = "coupon#" + userId;
        } else {
            this.userId = userId;
        }
        return this;
    }

    public CouponUsageEntity setCouponCode(String couponCode) {
        if (couponCode.startsWith("couponCode#")) {
            this.couponCode = couponCode;
        } else {
            this.couponCode = "couponCode#" + couponCode;
        }
        return this;
    }

}
