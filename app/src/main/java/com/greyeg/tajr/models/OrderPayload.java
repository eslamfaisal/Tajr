package com.greyeg.tajr.models;

public class OrderPayload {

     private String token;
     private String user_id;
     private String product_id;
     private String client_name;
     private String client_phone;
     private String city_id;
     private String client_area;
     private String client_address;
     private String items;
     private String discount;
     private String sender_name;
     private String sender_id;


    public OrderPayload() {
    }

    public String getToken() {
        return token;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getClient_area() {
        return client_area;
    }

    public String getClient_address() {
        return client_address;
    }

    public String getItems() {
        return items;
    }

    public String getDiscount() {
        return discount;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getSender_id() {
        return sender_id;
    }
}
