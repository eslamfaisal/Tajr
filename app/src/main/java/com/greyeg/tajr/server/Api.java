package com.greyeg.tajr.server;

import com.greyeg.tajr.models.ActivityHistory;
import com.greyeg.tajr.models.AddReasonResponse;
import com.greyeg.tajr.models.AdminRecordsResponse;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.BotBlocksResponse;
import com.greyeg.tajr.models.Broadcast;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.models.CardsResponse;
import com.greyeg.tajr.models.CartResponse;
import com.greyeg.tajr.models.CashRequestHistory;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.DeleteAddProductResponse;
import com.greyeg.tajr.models.MoneyHistory;
import com.greyeg.tajr.models.MoneyRequestResponse;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.OrderStatusHistoryResponse;
import com.greyeg.tajr.models.PointsHistory;
import com.greyeg.tajr.models.RemainingOrdersResponse;
import com.greyeg.tajr.models.SimpleOrderResponse;
import com.greyeg.tajr.models.SimpleResponse;
import com.greyeg.tajr.models.SubscriberInfo;
import com.greyeg.tajr.models.ToalAvailableBalance;
import com.greyeg.tajr.models.UpdateOrderResponse;
import com.greyeg.tajr.models.UpdateOrederNewResponse;
import com.greyeg.tajr.models.UploadPhoneResponse;
import com.greyeg.tajr.models.UploadVoiceResponse;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.models.UserResponse;
import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.order.models.CurrentOrderResponse;
import com.greyeg.tajr.order.models.SingleOrderProductsResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {

    // log in user client
    @FormUrlEncoded
    @POST("send_test/login")
    Call<UserResponse> login(@Field("username") String email, @Field("password") String password);

    // log in user client
    @FormUrlEncoded
    @POST("send_test/get_products")
    Call<AllProducts> getProducts(@Field("token") String token,
                                  @Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("send_test/get_status_history")
    Call<OrderStatusHistoryResponse> getSatusHistoryResponse(@Field("token") String token,
                                                             @Field("order_id") String order_id,
                                                             @Field("status_type") String status_type,
                                                             @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("send_test/available_cards")
    Call<CardsResponse> getCards(@Field("token") String token);

    @FormUrlEncoded
    @POST("send_test/set_active_time")
    Call<UserWorkTimeResponse> userWorkTime(@Field("token") String token,
                                            @Field("activity") String activity, @Field("user_id") String user_id);


    @Multipart
    @POST("send_test/upload_voice_notes")
    Call<UploadVoiceResponse> uploadVoice(@Part("token") RequestBody token,
                                          @Part("order_id") RequestBody order_id,
                                          @Part("call_duration") RequestBody call_duration,
                                          @Part MultipartBody.Part audio);

    @Headers({"Content-Type: application/json"})
    @GET("send_test/get_orders")
    Call<CurrentOrderResponse> getNewCurrentOrderResponce(@Query("token") String token);

    @FormUrlEncoded
    @POST("send_test/cpanel_login")
    Call<UserResponse> adminLogin(@Field("apiKey") String email, @Field("apiSecret") String password);

    @FormUrlEncoded
    @POST("send_test/logout")
    Call<UserResponse> logout(@Field("token") String token, @Field("user_id") String user_id);

    @Headers({"Content-Type: application/json"})
    @FormUrlEncoded
    @POST("send_test/fetch_orders")
    Call<UserOrders> getOrders(@Field("token") String token);


    // log in user client
    @FormUrlEncoded
    @POST("send_test/new_order")
    Call<NewOrderResponse> recordNewOrder(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("product_id") String product_id,
            @Field("client_name") String client_name,
            @Field("client_phone") String client_phone,
            @Field("city_id") String city_id,
            @Field("client_area") String client_area,
            @Field("client_address") String client_address,
            @Field("items") String items,
            @Field("discount") String discount,
            @Field("sender_name") String sender_name,
            @Field("sender_id") String sender_id
    );


    @FormUrlEncoded
    @POST("send_test/new_order")
    Call<ResponseBody> testNewOrder(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("product_id") String product_id,
            @Field("client_name") String client_name,
            @Field("client_phone") String client_phone,
            @Field("city_id") String city_id,
            @Field("client_area") String client_area,
            @Field("client_address") String client_address,
            @Field("items") String items,
            @Field("discount") String discount
    );

    @FormUrlEncoded
    @POST("send_test/update_order")
    Call<UpdateOrederNewResponse> updateOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("user_id") String user_id,
            @Field("status") String status

    );

    // TODO clean it with updateClientData()
    @FormUrlEncoded
    @POST("send_test/set_client_data")
    Call<SimpleOrderResponse> updateOrderData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("client_name") String client_name,
            @Field("client_address") String client_address,
            @Field("client_area") String client_area,
            @Field("notes") String notes

    );

    @FormUrlEncoded
    @POST("send_test/set_client_data")
    Call<CurrentOrderResponse> updateClientData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("client_name") String client_name,
            @Field("client_address") String client_address,
            @Field("client_area") String client_area,
            @Field("notes") String notes

    );


    @FormUrlEncoded
    @POST("send_test/set_order_data")
    Call<CurrentOrderResponse> updateSingleOrderData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("product_id") String product_id,
            @Field("client_city") String client_city,
            @Field("items_no") String items_no,
            @Field("discount") String discount

    );

    @FormUrlEncoded
    @POST("send_test/set_order_data")
    Call<CurrentOrderResponse> updateOrderMultiOrderData(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("client_city") String client_city,
            @Field("discount") String discount

    );

    @FormUrlEncoded
    @POST("send_test/remaining_orders")
    Call<RemainingOrdersResponse> getRemainingOrders(@Field("token") String token);

    @FormUrlEncoded
    @POST("send_test/add_product_to_order")
    Call<DeleteAddProductResponse> addProduct(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("product_id") String product_id,
            @Field("user_id") String user_id,
            @Field("items_no") String items_no
    );


    @FormUrlEncoded
    @POST("send_test/delete_product_from_order")
    Call<DeleteAddProductResponse> deleteProduct(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("extra_product_key") String extra_product_key,
            @Field("user_id") String user_id,
            @Field("product_id") String product_id
    );

    @FormUrlEncoded
    @POST("send_test/shipping_attempts")
    Call<UpdateOrederNewResponse> updateShippingOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("action") String action,
            @Field("user_id") String user_id

    );

    @FormUrlEncoded
    @POST("send_test/send_problem")
    Call<SimpleResponse> sendProblemForOrder(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("problem") String problem

    );


    @FormUrlEncoded
    @POST("send_test/phone")
    Call<UploadPhoneResponse> missedCall(@Field("token") String token,
                                         @Field("phone") String phone);

    @FormUrlEncoded
    @POST("send_test/update_order")
    Call<UpdateOrederNewResponse> updateDelayedOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("delayed_until") String delayed_until,
            @Field("user_id") String user_id,
            @Field("status") String status

    );


    ///////////////////////////////////////////////////////


    // log in user client
    @FormUrlEncoded
    @POST("send_test/send_problem")
    Call<UpdateOrderResponse> sendProblem(
            @Field("token") String token,
            @Field("user_id") int user_id,
            @Field("order_id") int order_id,
            @Field("problem") String problem

    );

    @Headers({"Content-Type: application/json"})
    @GET("send_test/get_orders")
    Call<SimpleOrderResponse> getFuckenOrders(@Query("token") String token);


    @FormUrlEncoded
    @POST("send_test/update_order")
    Call<UpdateOrederNewResponse> updateOrders(
            @Field("token") String token,
            @Field("order_id") String order_id,
            @Field("status") String status

    );


    @FormUrlEncoded
    @POST("send_test/confirm_shipper_status")
    Call<UpdateOrederNewResponse> confirm_shipper_status(
            @Field("token") String token,
            @Field("order_id") String order_id

    );

    // log in user client
    @FormUrlEncoded
    @POST("send_test/phone_search")
    Call<SimpleOrderResponse> getPhoneData(@Field("token") String token,
                                           @Field("user_id") String user_id,
                                           @Field("phone") String phone);

    // log in user client
    @FormUrlEncoded
    @POST("send_test/phone_search")
    Call<CurrentOrderResponse> getPhoneData2(@Field("token") String token,
                                           @Field("user_id") String user_id,
                                           @Field("phone") String phone);




    @FormUrlEncoded
    @POST("send_test/set_order_data")
    Call<SimpleOrderResponse> updateOrderCalculationsSingleOrder(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("product_id") String product_id,
            @Field("client_city") String client_city,
            @Field("items_no") String items_no,
            @Field("discount") String discount

    );

    @FormUrlEncoded
    @POST("send_test/set_order_data")
    Call<SimpleOrderResponse> updateOrderCalculationsMultiOrder(
            @Field("token") String token,
            @Field("user_id") String user_id,
            @Field("order_id") String order_id,
            @Field("client_city") String client_city,
            @Field("discount") String discount

    );


    // log in user client
    @FormUrlEncoded
    @POST("send_test/get_cities")
    Call<Cities> getCities(@Field("token") String token, @Field("user_id") String user_id);


    // log in user client
    @FormUrlEncoded
    @POST("send_test/get_products")
    Call<SingleOrderProductsResponse> getSingleOrderProducts(@Field("token") String token,
                                                             @Field("user_id") String user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send_test/activity_history")
    Call<ActivityHistory> getActivityHistory(@Field("token") String token,
                                             @Field("user_id") String user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send_test/points_history")
    Call<PointsHistory> getPointsHistory(@Field("token") String token,
                                         @Field("user_id") String user_id);

    // log in user client
    @FormUrlEncoded
    @POST("send_test/cash_history")
    Call<MoneyHistory> getMoneyHistory(@Field("token") String token);


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
    @POST("send_test/balance_history")
    Call<CashRequestHistory> getAvailableBalance(@Field("token") String token);

    // log in user client

    // log in user client
    @FormUrlEncoded
    @POST("send_test/retrieve_cards")
    Call<CartResponse> getCartDetails(@Field("token") String token, @Field("amount") String amount, @Field("type") String type);

    @FormUrlEncoded
    @POST("send_test/balance")
    Call<ToalAvailableBalance> getNormalAvailableBalance(@Field("token") String token, @Field("type") String type);

    @FormUrlEncoded
    @POST("send_test/balance")
    Call<ToalAvailableBalance> getCustomelAvailableBalance(@Field("token") String token,
                                                           @Field("type") String type,
                                                           @Field("year") String year,
                                                           @Field("month") String month,
                                                           @Field("day") String day
    );



    // log in user client
    @FormUrlEncoded
    @POST("send/all_records")
    Call<AdminRecordsResponse> getRecords(@Field("token") String token);

    @FormUrlEncoded
    @POST("send_test/get_subscriber_info")
    Call<SubscriberInfo> getSubscriberInfo(@Field("token") String token,@Field("sender_name") String sender_name);

    @FormUrlEncoded
    @POST("send_test/get_bot_blocks")
    Call<BotBlocksResponse> getBotBlocks(@Field("token") String token);

    @FormUrlEncoded
    @POST("send_test/broadcast")
    Call<Broadcast> sendBroadcast(@Field("token") String token, @Field("subscriber") String subscriber
            , @Field("page") String page, @Field("block") String block);

    @FormUrlEncoded
    @POST("send_test/cancellation_reasons")
    Call<CancellationReasonsResponse> getCancellationReasons(@Field("token") String token);

    @FormUrlEncoded
    @POST("send_test/add_cancellation_reason")
    Call<AddReasonResponse> submitNewCancellationReason(@Field("token") String token,@Field("name") String name);


}
