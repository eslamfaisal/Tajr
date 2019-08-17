package com.greyeg.tajr;

import android.util.Log;

import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import org.junit.Test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

public class SetActiveTimeTest {
    private static final String TAG = "SetActiveTimeTest";

    @Test
    public void setActivrTime() {
        BaseClient.getBaseClient().create(Api.class).userWorkTime("YIXRKEsDUv4VpAP5BaroqlJb", "55", "108")
                .enqueue(new Callback<UserWorkTimeResponse>() {
                    @Override
                    public void onResponse(Call<UserWorkTimeResponse> call, Response<UserWorkTimeResponse> response) {
                        assertEquals("1200", response.body().getCode());
                    }

                    @Override
                    public void onFailure(Call<UserWorkTimeResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        System.out.println(t.getMessage());
                    }
                });
    }
}
