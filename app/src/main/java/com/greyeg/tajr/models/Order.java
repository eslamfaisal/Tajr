package com.greyeg.tajr.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("product_name")
    @Expose
    private String product_name;

    @SerializedName("order_status")
    @Expose
    private String order_status;

    @SerializedName("discount")
    @Expose
    private String discount;

    @SerializedName("order_shipping_status_type")
    @Expose
    private String order_shipping_status_type;

    @SerializedName("order_shipping_status")
    @Expose
    private String order_shipping_status;

    @SerializedName("order_status_type")
    @Expose
    private String order_status_type;

    @SerializedName("client_name")
    @Expose
    private String client_name;

    @SerializedName("client_address")
    @Expose
    private String client_address;

    @SerializedName("client_area")
    @Expose
    private String client_area;

    @SerializedName("client_city")
    @Expose
    private String client_city;

    @SerializedName("client_city_id")
    @Expose
    private String client_city_id;

    @SerializedName("phone_1")
    @Expose
    private String phone_1;

    @SerializedName("phone_2")
    @Expose
    private String phone_2 ;

    @SerializedName("item_cost")
    @Expose
    private String item_cost;

    @SerializedName("items_no")
    @Expose
    private String items_no;

    @SerializedName("shipping_cost")
    @Expose
    private String shipping_cost;

    @SerializedName("order_cost")
    @Expose
    private String order_cost;

    @SerializedName("total_order_cost")
    @Expose
    private String total_order_cost;

    @SerializedName("fb_sender_id")
    @Expose
    private String fb_sender_id;

    @SerializedName("sender_name")
    @Expose
    private String sender_name;

    @SerializedName("client_feedback")
    @Expose
    private String client_feedback;

    @SerializedName("order_type")
    @Expose
    private String order_type;

//    @SerializedName("extra_data")
//    @Expose
//    private String extra_data;

    @SerializedName("multi_orders")
    @Expose
    private String multi_orders ;

    public Order() {
    }

    public String getDiscount() {
        return discount;
    }

    public String getId() {
        return id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getOrder_shipping_status_type() {
        return order_shipping_status_type;
    }

    public String getOrder_shipping_status() {
        return order_shipping_status;
    }

    public String getOrder_status_type() {
        return order_status_type;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getClient_address() {
        return client_address;
    }

    public String getClient_area() {
        return client_area;
    }

    public String getClient_city() {
        return client_city;
    }

    public String getPhone_1() {
        return phone_1;
    }

    public String getPhone_2() {
        return phone_2;
    }

    public String getItem_cost() {
        return item_cost;
    }

    public String getItems_no() {
        return items_no;
    }

    public String getShipping_cost() {
        return shipping_cost;
    }

    public String getOrder_cost() {
        return order_cost;
    }

    public String getTotal_order_cost() {
        return total_order_cost;
    }

    public String getFb_sender_id() {
        return fb_sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getClient_feedback() {
        return client_feedback;
    }

    public String getOrder_type() {
        return order_type;
    }
//
//    public String getExtra_data() {
//        return extra_data;
//    }

    public String getMulti_orders() {
        return multi_orders;
    }

    public String getClient_city_id() {
        return client_city_id;
    }
}
