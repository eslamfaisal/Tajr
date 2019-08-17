package com.greyeg.tajr;

import android.util.Log;

import com.greyeg.tajr.models.OrderStatusHistoryResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import org.junit.Test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SatusHistoryTest {

    private static final String TAG = "SatusHistoryTest";

    @Test
    public void getStatusHistoryTest() {

        BaseClient.getBaseClient().create(Api.class)
                .getSatusHistoryResponse("YIXRKEsDUv4VpAP5BaroqlJb", "8297", "order", "108")
                .enqueue(new Callback<OrderStatusHistoryResponse>() {
                    @Override
                    public void onResponse(Call<OrderStatusHistoryResponse> call, Response<OrderStatusHistoryResponse> response) {
                        System.out.println(response.body().toString());
                        Log.d(TAG, "onResponse: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<OrderStatusHistoryResponse> call, Throwable t) {
                        System.out.println(t.getMessage());
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }


}
