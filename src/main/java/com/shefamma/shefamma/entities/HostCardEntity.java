package com.shefamma.shefamma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostCardEntity {
    HostEntity hostEntity;
    @Getter
    private List<MealEntity> meals;
    public void setMeals(List<MealEntity> meals) {
        this.meals = meals;
    }


























}
