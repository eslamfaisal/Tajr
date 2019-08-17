package com.greyeg.tajr;

import android.util.Log;

import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import org.junit.Test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetCitiesTest {

    private static final String TAG = "GetCitiesTest";

    @Test
    public void getCitiesTest() {

        BaseClient.getBaseClient().create(Api.class).getCities("YIXRKEsDUv4VpAP5BaroqlJb", "108")
                .enqueue(new Callback<Cities>() {
                    @Override
                    public void onResponse(Call<Cities> call, Response<Cities> response) {
                        System.out.println(response.body().toString());
                        Log.d(TAG, "onResponse: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<Cities> call, Throwable t) {

                        System.out.println(t.getMessage());
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }
}
