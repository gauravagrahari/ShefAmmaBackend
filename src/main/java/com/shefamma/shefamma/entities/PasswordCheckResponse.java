package com.shefamma.shefamma.entities;

public class PasswordCheckResponse {
    private boolean isPasswordCorrect;
    private String timeStamp;

    public PasswordCheckResponse(boolean isPasswordCorrect, String timeStamp) {
        this.isPasswordCorrect = isPasswordCorrect;
        this.timeStamp = timeStamp;
    }

    // Getters
    public boolean isPasswordCorrect() {
        return isPasswordCorrect;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
