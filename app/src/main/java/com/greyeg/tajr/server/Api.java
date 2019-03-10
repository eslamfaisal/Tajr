package com.greyeg.tajr.server;

import com.greyeg.tajr.models.ActivityHistory;
import com.greyeg.tajr.models.AdminRecordsResponse;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.CardsResponse;
import com.greyeg.tajr.models.CartResponse;
import com.greyeg.tajr.models.CashRequestHistory;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.CurrentOrderResponse;
import com.greyeg.tajr.models.DeleteAddProductResponse;
import com.greyeg.tajr.models.MoneyHistory;
import com.greyeg.tajr.models.MoneyRequestResponse;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.PointsHistory;
import com.greyeg.tajr.models.SimpleOrderResponse;
import com.greyeg.tajr.models.ToalAvailableBalance;
import com.greyeg.tajr.models.UpdateOrderResponse;
import com.greyeg.tajr.models.UpdateOrdreDataRespnse;
import com.greyeg.tajr.models.UpdateOrederNewResponse;
import com.greyeg.tajr.models.UploadPhoneResponse;
import com.greyeg.tajr.models.UploadVoiceResponse;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.models.UserResponse;
import com.greyeg.tajr.models.UserWorkTimeResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {

    // log in user client
    @FormUrlEncoded
    @POST("send/login")
    Call<UserResponse> login(@Field("username") String email, @Field("password") String password);

    // log in user client
    @FormUrlEncoded
    @POST("send/cpanel_login")
    Call<UserResponse> adminLogin(@Field("apiKey") String email, @Field("apiSecret") String password);

    // log in user client
    @FormUrlEncoded
    @POST("send/logout")
    Call<UserResponse> logout(@Field("token") String token, @Field("user_id") String user_id);

    @Headers({"Content-Type: application/json"})
    @FormUrlEncoded
    @POST("send/fetch_orders")
    Call<UserOrders> getOrders(@Field("token") String token);

    @Headers({"Content-Type: application/json"})
    @POST("send/get_orders")
    Call<SimpleOrderResponse> getFuckenOrders(@Query("token")String token);

    // log in user client
    @Multipart
    @POST("send/fetch_orders")
    Call<CurrentOrderResponse> getCurrentOrder(@Part("token") RequestBody token);

    @FormUrlEncoded
    @POST("send/update_order")
    Call<UpdateOrederNewResponse> updateOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("user_id") String user_id,
            @Field("status") String status

    );

    @FormUrlEncoded
    @POST("send/update_order")
    Call<UpdateOrederNewResponse> updateOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("status") String status

    );

    @FormUrlEncoded
    @POST("send/update_order")
    Call<UpdateOrederNewResponse> updateDelayedOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("delayed_until") String delayed_until,
            @Field("user_id") String user_id,
            @Field("status") String status

    );

    @FormUrlEncoded
    @POST("send/confirm_shipper_status")
    Call<UpdateOrederNewResponse> confirm_shipper_status(
            @Field("token") String token,
            @Field("order_id") String order_id

    );

    // log in user client
    @FormUrlEncoded
    @POST("send/get_phone")
    Call<SimpleOrderResponse> getPhoneData(@Field("token") String token,
                                          @Field("user_id") String user_id,
                                          @Field("phone") String phone);


    @FormUrlEncoded
    @POST("send/phone")
    Call<UploadPhoneResponse> uploadPhone(@Field("token") String token,
                                          @Field("phone") String phone);


    // log in user client
    @FormUrlEncoded
    @POST("send/set_active_time")
    Call<UserWorkTimeResponse> userWorkTime(@Field("token") String token,
                                            @Field("user_id") String user_id,
                                            @Field("activity") String activity);

    @FormUrlEncoded
    @POST("send/set_data")
    Call<UpdateOrdreDataRespnse> updateOrderData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("product_id") String product_id,
            @Field("client_city") String client_city,
            @Field("client_name") String client_name,
            @Field("client_address") String client_address,
            @Field("client_area") String client_area,
            @Field("items_no") String items_no,
            @Field("notes") String notes,
            @Field("discount") String discount,
            @Field("order_cost") String order_cost,
            @Field("total_order_cost") String total_order_cost

            );

    @FormUrlEncoded
    @POST("send/set_data")
    Call<UpdateOrdreDataRespnse> updateOrderData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("client_city") String client_city,
            @Field("client_name") String client_name,
            @Field("client_address") String client_address,
            @Field("client_area") String client_area,
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
    Call<MoneyHistory> getMoneyHistory(@Field("token") String token);

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

    // log in user client
    @FormUrlEncoded
    @POST("send/cash_out")
    Call<MoneyRequestResponse> requestCash(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("mobile") String mobile,
            @Field("cash") String cash,
            @Field("notes") String notes
    );

    // log in user client
    @FormUrlEncoded
    @POST("send/balance_history")
    Call<CashRequestHistory> getAvailableBalance(@Field("token") String token);

    // log in user client
    @FormUrlEncoded
    @POST("send/available_cards")
    Call<CardsResponse> getCarts(@Field("token") String token);

     // log in user client
    @FormUrlEncoded
    @POST("send/retrieve_cards")
    Call<CartResponse> getCartDetails(@Field("token") String token, @Field("amount") String amount, @Field("type") String type);

    @FormUrlEncoded
    @POST("send/balance")
    Call<ToalAvailableBalance> getTotalAvailableBalance(@Field("token") String token);

    @Multipart
    @POST("send/upload_voice_notes")
    Call<UploadVoiceResponse> uploadVoice(@Part("token") RequestBody token,
                                          @Part("order_id") RequestBody order_id,
                                          @Part("call_duration") RequestBody call_duration,
                                          @Part MultipartBody.Part audio);

    // log in user client
    @FormUrlEncoded
    @POST("send/all_records")
    Call<AdminRecordsResponse> getRecords(@Field("token") String token);

    @FormUrlEncoded
    @POST("send/delete_product_from_order")
    Call<DeleteAddProductResponse> deleteProduct(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("extra_product_key") String extra_product_key,
            @Field("user_id") String user_id,
            @Field("product_id") String product_id
    );

    @FormUrlEncoded
    @POST("send/add_product_to_order")
    Call<DeleteAddProductResponse> addProduct(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("product_id") String product_id,
            @Field("user_id") String user_id,
            @Field("items_no") int items_no
    );


}
