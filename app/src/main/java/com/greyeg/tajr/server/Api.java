package com.greyeg.tajr.server;

import com.greyeg.tajr.models.PhonNumberResponse;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.models.UserResponse;
import com.greyeg.tajr.models.UserWorkTimeResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    // log in user client
    @FormUrlEncoded
    @POST("send/login")
    Call<UserResponse> login(@Field("username") String email, @Field("password") String password);

    // log in user client
    @FormUrlEncoded
    @POST("send/logout")
    Call<UserResponse> logout(@Field("token") String token, @Field("user_id") String user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/fetch_orders")
    Call<UserOrders> getOrders(@Field("token") String token, @Field("user_id") String user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/update_order")
    Call<UserOrders> updateOrders(
            @Field("token") String token,
            @Field("user_id") int user_id,
            @Field("order_id") int order_id,
            @Field("status") String status

    );

    // log in user client
    @FormUrlEncoded
    @POST("send/logout")
    Call<PhonNumberResponse> getPhoneData(@Field("token") String token, @Field("user_id") String user_id, @Field("phone") String phone);


    // log in user client
    @FormUrlEncoded
    @POST("send/set_active_time")
    Call<UserWorkTimeResponse> userWorkTime(@Field("token") String token,
                                            @Field("user_id") String user_id,
                                            @Field("activity") String activity);



}
