package com.raksul.users.model;

public class DmtResponce {
    int status;
    String message;
    Object result;

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}