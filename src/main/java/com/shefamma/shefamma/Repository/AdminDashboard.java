package com.shefamma.shefamma.Repository;

import com.shefamma.shefamma.entities.AdminDashboardEntity;
import com.shefamma.shefamma.entities.DevBoyEntity;
import com.shefamma.shefamma.entities.HostEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminDashboard {
    List<DevBoyEntity> getAllDevBoysIds();

    List<HostEntity> getAllHosts();

    ResponseEntity<String> login(AdminDashboardEntity adminDashboardEntity);

    boolean isPasswordCorrect(String uuidHost, String oldPassword);

    ResponseEntity<String> saveSignup(AdminDashboardEntity adminDashboardEntity);

    List<DevBoyEntity> getAllDevBoys();

    List<HostEntity>  getAllHostsIds();
}