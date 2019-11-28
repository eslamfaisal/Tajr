package com.greyeg.tajr.repository;

import com.greyeg.tajr.models.CallTimePayload;
import com.greyeg.tajr.models.CallTimeResponse;
import com.greyeg.tajr.server.BaseClient;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class CallTimeRepo {

    private static CallTimeRepo callTimeRepo;

    public static CallTimeRepo getInstance() {
        return callTimeRepo==null?callTimeRepo=new CallTimeRepo():callTimeRepo;
    }

    public Single<Response<CallTimeResponse>> setCallTime(CallTimePayload callTimePayload){
        return BaseClient
                .getApiService()
                .setCallTime(callTimePayload);
    }
}
