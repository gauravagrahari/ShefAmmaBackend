package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.CouponEntity;
import com.shefamma.shefamma.entities.CouponUsageEntity;

public interface Coupon {
    CouponEntity createCapacity(CouponEntity couponEntity);

    CouponUsageEntity createCapacity(CouponUsageEntity couponEntity);

    String checkCouponAvailability(String userId, String couponCode);

    String useCoupon(String userId, String couponCode);
}
