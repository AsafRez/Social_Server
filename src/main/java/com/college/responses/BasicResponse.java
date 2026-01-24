package com.college.responses;

public class BasicResponse {
    private boolean status;
    private Integer errorCode;

    public BasicResponse(boolean success, Integer errorCode) {
        this.status = success;
        this.errorCode = errorCode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
