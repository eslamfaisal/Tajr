package com.greyeg.tajr.server;

import com.greyeg.tajr.models.ActivityHistory;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.MoneyHistory;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.PhonNumberResponse;
import com.greyeg.tajr.models.PointsHistory;
import com.greyeg.tajr.models.UpdateOrderResponse;
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
    Call<UpdateOrderResponse> updateOrders(
            @Field("token") String token,
            @Field("user_id") int user_id,
            @Field("order_id") int order_id,
            @Field("status") String status

    );


    // log in user client
    @FormUrlEncoded
    @POST("send/get_phone")
    Call<UserOrders> getPhoneData(@Field("token") String token,
                                          @Field("user_id") String user_id,
                                          @Field("phone") String phone);


    // log in user client
    @FormUrlEncoded
    @POST("send/set_active_time")
    Call<UserWorkTimeResponse> userWorkTime(@Field("token") String token,
                                            @Field("user_id") String user_id,
                                            @Field("activity") String activity);

    // log in user client
    @FormUrlEncoded
    @POST("send/logout")
    Call<PhonNumberResponse> updateOrderData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("client_city") String client_city,
            @Field("client_name") String client_name,
            @Field("client_address") String client_address,
            @Field("client_area") String client_area,
            @Field("items_no") String items_no,
            @Field("notes") String notes,
            @Field("discount") String discount
    );

    // log in user client
    @FormUrlEncoded
    @POST("send/send_problem")
    Call<UpdateOrderResponse> sendProblem(
            @Field("token") String token,
            @Field("user_id") int user_id,
            @Field("order_id") int order_id,
            @Field("problem") String problem

    );

    // log in user client
    @FormUrlEncoded
    @POST("send/get_cities")
    Call<Cities> getCities(@Field("token") String token, @Field("user_id") String user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/get_products")
    Call<AllProducts> getProducts(@Field("token") String token,
                              @Field("user_id") String  user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/activity_history")
    Call<ActivityHistory> getActivityHistory(@Field("token") String token,
                                             @Field("user_id") String  user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/points_history")
    Call<PointsHistory> getPointsHistory(@Field("token") String token,
                                         @Field("user_id") String  user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/cash_history")
    Call<MoneyHistory> getMoneyHistory(@Field("token") String token,
                                       @Field("user_id") String  user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send/new_order")
    Call<NewOrderResponse> recordNewOrder(
            @Field("token") String token,
            @Field("user_id") int user_id,
            @Field("product_id") int product_id,
            @Field("client_name") String client_name,
            @Field("client_phone") String client_phone,
            @Field("city_id") int city_id,
            @Field("client_area") String client_area,
            @Field("client_address") String client_address,
            @Field("items") String items
    );
}
