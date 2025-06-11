package com.iyuba.toelflistening.java.model.bean;

import com.google.gson.annotations.SerializedName;

public class WxLoginBean {


    @SerializedName("result")
    private int result;
    @SerializedName("message")
    private String message;
    @SerializedName("token")
    private String token;
    @SerializedName("uid")
    private int uid;


    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
