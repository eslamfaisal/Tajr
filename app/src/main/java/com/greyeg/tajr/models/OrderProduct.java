package com.greyeg.tajr.models;

import java.util.ArrayList;

public class OrderProduct {

    private int key;
    private String id;
    private String name;
    private int price;
    private int items_no;
    private String image;
    private String cost;
    ArrayList<ProductExtra> extras;

    public int getKey() {
        return key;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getItems_no() {
        return items_no;
    }

    public String getImage() {
        return image;
    }

    public String getCost() {
        return cost;
    }

    public ArrayList<ProductExtra> getExtras() {
        return extras;
    }
}
