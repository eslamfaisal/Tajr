package com.greyeg.tajr.order;

import com.greyeg.tajr.order.models.CurrentOrderResponse;
import com.greyeg.tajr.order.models.SingleOrderProductsResponse;

public class CurrentOrderData {

    public static CurrentOrderData currentOrderData;

    private CurrentOrderResponse currentOrderResponse;
    private SingleOrderProductsResponse singleOrderProductsResponse;

    public static CurrentOrderData getInstance(){
        if (currentOrderData!=null){
            return currentOrderData;
        }else {
            currentOrderData = new CurrentOrderData();
            return currentOrderData;
        }
    }

    public CurrentOrderResponse getCurrentOrderResponse() {
        return currentOrderResponse;
    }

    public void setCurrentOrderResponse(CurrentOrderResponse currentOrderResponse) {
        this.currentOrderResponse = currentOrderResponse;
    }

    public SingleOrderProductsResponse getSingleOrderProductsResponse() {
        return singleOrderProductsResponse;
    }

    public void setSingleOrderProductsResponse(SingleOrderProductsResponse singleOrderProductsResponse) {
        this.singleOrderProductsResponse = singleOrderProductsResponse;
    }
}
