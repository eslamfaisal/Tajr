package com.greyeg.tajr.models;

import androidx.annotation.Nullable;

public class CartItem {

    private ProductData product;
    private int quantity;

    public CartItem(ProductData product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(ProductData product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj==null) return false;
        if (!(obj instanceof CartItem)) return false;
        return ((CartItem) obj).product.equals(this.product);
    }
}
