package com.social.responses;

import com.social.Entity.User;

public class UserResponse extends BasicResponse{
    private User user;
    private String token;

    public UserResponse(boolean success, Integer errorCode, User user, String token) {
        super(success, errorCode);
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public UserResponse(boolean success, Integer errorCode, User user) {
        super(success, errorCode);
        this.user = user;
    }
    public UserResponse(boolean success, Integer errorCode) {
        super(success, errorCode);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
