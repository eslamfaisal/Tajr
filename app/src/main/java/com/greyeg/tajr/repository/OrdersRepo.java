package com.greyeg.tajr.repository;

import android.content.res.Resources;

import androidx.lifecycle.MutableLiveData;

import com.crashlytics.android.Crashlytics;
import com.greyeg.tajr.R;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.OrderPayload;
import com.greyeg.tajr.order.models.CurrentOrderResponse;
import com.greyeg.tajr.server.BaseClient;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersRepo {

    private static OrdersRepo ordersRepo;

    private MutableLiveData<CurrentOrderResponse> currentOrder;
    private MutableLiveData<Boolean> isCurrentOrderLoading=new MutableLiveData<>();
    private MutableLiveData<String> currentOrderLoadingError=new MutableLiveData<>();


    public static OrdersRepo getInstance() {
        return ordersRepo==null?ordersRepo=new OrdersRepo():ordersRepo;
    }

    private OrdersRepo() {
    }

    public Single<Response<NewOrderResponse>> makeNewOrder(OrderPayload orderPayload){
        return BaseClient
                .getApiService()
                .makeNewOrder(orderPayload);

    }

    public MutableLiveData<CurrentOrderResponse> getCurrentOrder(String token) {
        currentOrder=new MutableLiveData<>();
        isCurrentOrderLoading.setValue(true);
        BaseClient.getApiService()
                .getNewCurrentOrder(token)
                .enqueue(new Callback<CurrentOrderResponse>() {
                    @Override
                    public void onResponse(Call<CurrentOrderResponse> call, Response<CurrentOrderResponse> response) {
                        CurrentOrderResponse currentOrderResponse=response.body();
                        if (response.isSuccessful()&&currentOrderResponse!=null){
                            currentOrder.setValue(currentOrderResponse);
                        }else {
                            currentOrderLoadingError.setValue(Resources.getSystem().getString(R.string.server_error));
                        }
                        isCurrentOrderLoading.setValue(false);
                    }

                    @Override
                    public void onFailure(Call<CurrentOrderResponse> call, Throwable t) {
                        isCurrentOrderLoading.setValue(false);
                        currentOrderLoadingError.setValue(Resources.getSystem().getString(R.string.server_error));
                    }
                });
        return currentOrder;
    }

    public MutableLiveData<Boolean> getIsCurrentOrderLoading() {
        return isCurrentOrderLoading;
    }

    public MutableLiveData<String> getCurrentOrderLoadingError() {
        return currentOrderLoadingError;
    }
}
