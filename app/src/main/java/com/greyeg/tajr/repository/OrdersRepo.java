package com.greyeg.tajr.repository;

import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.OrderPayload;
import com.greyeg.tajr.server.BaseClient;

import io.reactivex.Single;
import retrofit2.Response;

public class OrdersRepo {

    private static OrdersRepo ordersRepo;

    public static OrdersRepo getInstance() {
        return ordersRepo==null?ordersRepo=new OrdersRepo():ordersRepo;
    }

    private OrdersRepo() {
    }

    public Single<Response<NewOrderResponse>> makeNewOrder(OrderPayload orderPayload){
        return BaseClient
                .getService()
                .makeNewOrder(orderPayload);

    }
}
