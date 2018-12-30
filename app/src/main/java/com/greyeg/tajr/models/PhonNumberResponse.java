package com.greyeg.tajr.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhonNumberResponse {

    @SerializedName("response")
    @Expose
    private String response;

    @SerializedName("order")
    @Expose
    private Order order;

    @SerializedName("notes")
    @Expose
    private Order notes;

    public PhonNumberResponse() {
    }

    public String getResponse() {
        return response;
    }

    public Order getOrder() {
        return order;
    }

    public Order getNotes() {
        return notes;
    }
}
