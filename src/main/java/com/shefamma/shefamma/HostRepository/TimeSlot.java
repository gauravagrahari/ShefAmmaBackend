package com.shefamma.shefamma.HostRepository;

import com.shefamma.shefamma.entities.ItemEntity;
import com.shefamma.shefamma.entities.TimeSlotEntity;

public interface TimeSlot {
    TimeSlotEntity saveSlotTime(TimeSlotEntity timeentity);

    TimeSlotEntity getTimeSlot(String timehostId, TimeSlotEntity timeentity);

    TimeSlotEntity updateTimeSlot(String timehostId, TimeSlotEntity timeentity);
}
