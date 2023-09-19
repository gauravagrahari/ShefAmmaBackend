package com.shefamma.shefamma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostCardEntity {
    HostEntity hostEntity;
    List<String> itemNames;
    List<String> mealTypes; // Add List<String> for mealTypes

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    public List<String> getMealTypes() {
        return mealTypes;
    }

    public void setMealTypes(List<String> mealTypes) {
        this.mealTypes = mealTypes;
    }
}
