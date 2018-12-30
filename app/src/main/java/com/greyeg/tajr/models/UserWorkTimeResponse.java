package com.greyeg.tajr.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserWorkTimeResponse {

    @SerializedName("Response")
    @Expose
    private String Response;

    @SerializedName("data")
    @Expose
    private String  data;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("info")
    @Expose
    private String info;


    public UserWorkTimeResponse() {
    }

    public String getResponse() {
        return Response;
    }

    public String getData() {
        return data;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
