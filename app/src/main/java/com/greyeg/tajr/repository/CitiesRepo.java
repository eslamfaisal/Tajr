package com.greyeg.tajr.repository;

import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.server.BaseClient;
import io.reactivex.Single;
import retrofit2.Response;

public class CitiesRepo {

    private static CitiesRepo citiesRepo;

    public static CitiesRepo getInstance() {
        return citiesRepo==null?citiesRepo=new CitiesRepo():citiesRepo;
    }

    private CitiesRepo() {

    }

    public Single<Response<Cities>> getCities(String token, String user_id){
        Single<Response<Cities>> cities=
                BaseClient
                .getService()
                .getCities2(token,user_id);

        return cities;
    }
}
