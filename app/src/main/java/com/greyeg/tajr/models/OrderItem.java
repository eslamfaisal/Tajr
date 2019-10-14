package com.greyeg.tajr.models;

import androidx.annotation.Nullable;

public class OrderItem {

    private String product_id;
    private int items;

    public OrderItem(String product_id, int items) {
        this.product_id = product_id;
        this.items = items;
    }

    public OrderItem(String product_id) {
        this.product_id = product_id;
    }

    public OrderItem() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public int getItems() {
        return items;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setItems(int items) {
        this.items = items;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null)return false;
        if (!(obj instanceof OrderItem)) return false;
        OrderItem orderItem= (OrderItem) obj;

        return orderItem.product_id.equals(this.product_id);
    }
}
