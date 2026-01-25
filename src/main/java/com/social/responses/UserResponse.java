package com.social.responses;

import com.social.Classes.User;

public class UserResponse extends BasicResponse{
    private User user;
    private String token;

    public UserResponse(boolean success, Integer errorCode, User user, String token) {
        super(success, errorCode);
        this.user = user;
        this.token = token;
    }

    public UserResponse(boolean success, Integer errorCode, User user) {
        super(success, errorCode);
        this.user = user;
    }
}
