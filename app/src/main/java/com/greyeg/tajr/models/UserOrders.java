package com.greyeg.tajr.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserOrders {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("info")
    @Expose
    private String info;

    @SerializedName("order_type")
    @Expose
    private String order_type;

    @SerializedName("response")
    @Expose
    private String response;

    @SerializedName("order")
    @Expose
    private Order order;

    @SerializedName("notes")
    @Expose
    private Notes notes;

    public UserOrders() {
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getOrder_type() {
        return order_type;
    }

    public String getResponse() {
        return response;
    }

    public Order getOrder() {
        return order;
    }

    public Notes getNotes() {
        return notes;
    }
}
