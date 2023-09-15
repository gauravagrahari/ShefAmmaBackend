package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.AdminDashboardEntity;
import org.springframework.http.ResponseEntity;

public interface AdminDashboard {
    ResponseEntity<String> login(AdminDashboardEntity adminDashboardEntity);
    boolean isPasswordCorrect(String uuidHost, String oldPassword);

    ResponseEntity<String> saveSignup(AdminDashboardEntity adminDashboardEntity);
}
