package com.shefamma.shefamma.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.shefamma.shefamma.entities.CouponEntity;
import com.shefamma.shefamma.entities.CouponUsageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository

public class CouponImpl implements Coupon{
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Override
    public CouponEntity createCapacity(CouponEntity couponEntity) {
        dynamoDBMapper.save(couponEntity);
        return couponEntity;
    }
    @Override
    public CouponUsageEntity createCapacity(CouponUsageEntity couponEntity) {
        dynamoDBMapper.save(couponEntity);
        return couponEntity;
    }
    @Override
    public String checkCouponAvailability(String userId, String couponCode) {
        CouponEntity coupon = dynamoDBMapper.load(CouponEntity.class, "couponId#" + couponCode);
        if (coupon == null) {
            return "Coupon does not exist.";
        }

        CouponUsageEntity usage = dynamoDBMapper.load(CouponUsageEntity.class, "coupon#" + userId, "couponCode#" + couponCode);
        if (usage == null) {
            if (coupon.getUsedByCount() < coupon.getTotalEmployees()) {
                return "Available!";
            } else {
                return "Coupon usage limit for the company has been reached.";
            }
        } else if (usage.getUses() < coupon.getMaxUsesPerEmployee()) {
            return "Available!";
        } else {
            return "Ypu have reached the maximum usage limit for this coupon.";
        }
    }

    @Override
    public String useCoupon(String userId, String couponCode) {
        CouponEntity coupon = dynamoDBMapper.load(CouponEntity.class, "couponId#" + couponCode);
        if (coupon == null) {
            return "Coupon does not exist.";
        }

        CouponUsageEntity usage = dynamoDBMapper.load(CouponUsageEntity.class, "coupon#" + userId, "couponCode#" + couponCode);
        if (usage == null) {
            if (coupon.getUsedByCount() < coupon.getTotalEmployees()) {
                // Create new usage without assigning it to newUsage variable
                CouponUsageEntity newUsage = new CouponUsageEntity();
                newUsage.setUserId(userId).setCouponCode(couponCode).setUses(1);
                dynamoDBMapper.save(newUsage);

                coupon.setUsedByCount(coupon.getUsedByCount() + 1);
                dynamoDBMapper.save(coupon);
                return "Coupon used successfully for the first time.";
            } else {
                return "Coupon usage limit for the company has been reached.";
            }
        } else if (usage.getUses() < coupon.getMaxUsesPerEmployee()) {
            usage.setUses(usage.getUses() + 1);
            dynamoDBMapper.save(usage);
            return "Coupon use incremented.";
        } else {
            return "User has reached the maximum usage limit for this coupon.";
        }
    }

}
