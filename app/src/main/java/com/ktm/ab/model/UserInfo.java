package com.ktm.ab.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikhil on 08-03-2017.
 */

public class UserInfo {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("duke_200")
    @Expose
    private String duke200;
    @SerializedName("duke_200_version")
    @Expose
    private int duke200Version;
    @SerializedName("duke_250")
    @Expose
    private String duke250;
    @SerializedName("duke_250_version")
    @Expose
    private int duke250Version;
    @SerializedName("duke_390")
    @Expose
    private String duke390;
    @SerializedName("duke_390_version")
    @Expose
    private int duke390Version;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDuke200() {
        return duke200;
    }

    public void setDuke200(String duke200) {
        this.duke200 = duke200;
    }

    public int getDuke200Version() {
        return duke200Version;
    }

    public void setDuke200Version(int duke200Version) {
        this.duke200Version = duke200Version;
    }

    public String getDuke250() {
        return duke250;
    }

    public void setDuke250(String duke250) {
        this.duke250 = duke250;
    }

    public int getDuke250Version() {
        return duke250Version;
    }

    public void setDuke250Version(int duke250Version) {
        this.duke250Version = duke250Version;
    }

    public String getDuke390() {
        return duke390;
    }

    public void setDuke390(String duke390) {
        this.duke390 = duke390;
    }

    public int getDuke390Version() {
        return duke390Version;
    }

    public void setDuke390Version(int duke390Version) {
        this.duke390Version = duke390Version;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", userName='" + userName + '\'' +
                ", duke200='" + duke200 + '\'' +
                ", duke200Version='" + duke200Version + '\'' +
                ", duke250='" + duke250 + '\'' +
                ", duke250Version='" + duke250Version + '\'' +
                ", duke390='" + duke390 + '\'' +
                ", duke390Version='" + duke390Version + '\'' +
                '}';
    }
}
