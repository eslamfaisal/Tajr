package com.greyeg.tajr.models;

public class OrderItem {

    private String product_id;
    private int items;

    public OrderItem(String product_id, int items) {
        this.product_id = product_id;
        this.items = items;
    }

    public OrderItem() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public int getItems() {
        return items;
    }
}
