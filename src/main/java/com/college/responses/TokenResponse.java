package com.college.responses;

public class TokenResponse extends BasicResponse {
    private String  token;

    public TokenResponse(boolean success, Integer errorCode, String token) {
        super(success, errorCode);
        this.token = token;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
