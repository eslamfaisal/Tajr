package com.greyeg.tajr.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllProducts {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("info")
    @Expose
    private String info;

    @SerializedName("products_count")
    @Expose
    private String products_count;

    @SerializedName("products")
    @Expose
    private List<ProductData> products;

    public AllProducts() {
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getProducts_count() {
        return products_count;
    }

    public List<ProductData> getProducts() {
        return products;
    }
}
