package com.shefamma.shefamma.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ShefAmma")
public class CouponEntity {

    @DynamoDBHashKey(attributeName = "pk")
    private String couponId; // Unique coupon code, pk: "couponId#<CouponCode>"

    @DynamoDBAttribute(attributeName = "companyId")
    private String companyId; // Associated company ID

    @DynamoDBAttribute(attributeName = "totalEmployees")
    private int totalEmployees; // Total number of employees in the company

    @DynamoDBAttribute(attributeName = "maxUsesPerEmployee")
    private int maxUsesPerEmployee; // Maximum number of times each employee can use the coupon

    @DynamoDBAttribute(attributeName = "discountPercentage")
    private double discountPercentage; // Discount percentage

    @DynamoDBAttribute(attributeName = "usedByCount")
    private int usedByCount; // Number of unique users who have used the coupon

    @DynamoDBAttribute(attributeName = "validFrom")
    private String validFrom; // Coupon validity start date

    @DynamoDBAttribute(attributeName = "validUntil")
    private String validUntil; // Coupon validity end date

    @DynamoDBAttribute(attributeName = "minPurchaseAmount")
    private double minPurchaseAmount; // Minimum purchase amount required to use the coupon

    public CouponEntity setCouponId(String couponId) {
        if (couponId.startsWith("couponId#")) {
            this.couponId = couponId;
        } else {
            this.couponId = "couponId#" + couponId;
        }
        return this;
    }
    // Additional getters and setters...
}
