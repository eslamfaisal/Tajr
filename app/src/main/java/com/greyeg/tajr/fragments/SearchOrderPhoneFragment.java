package com.greyeg.tajr.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.Order;
import com.greyeg.tajr.models.UpdateOrderResponse;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.thefinestartist.movingbutton.MovingButton;
import com.thefinestartist.movingbutton.enums.ButtonPosition;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.greyeg.tajr.activities.LoginActivity.IS_LOGIN;
import static com.greyeg.tajr.activities.OrderActivity.CANCEL_ORDER_NAME;
import static com.greyeg.tajr.activities.OrderActivity.CLIENT_PROBLEM;
import static com.greyeg.tajr.activities.OrderActivity.CONFIRM_ORDER_NAME;
import static com.greyeg.tajr.activities.OrderActivity.askToFinishWork;
import static com.greyeg.tajr.activities.OrderActivity.client_cancel;
import static com.greyeg.tajr.activities.OrderActivity.finishTheWorkNow;
import static com.greyeg.tajr.activities.OrderActivity.order_data_confirmed;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchOrderPhoneFragment extends Fragment {

    @BindView(R.id.phone_input)
    EditText phoneInput;

    @BindView(R.id.search_btn)
    FloatingActionButton searchButton;


    @BindView(R.id.product)
    EditText product;

    @BindView(R.id.client_name)
    EditText client_name;

    @BindView(R.id.client_address)
    EditText client_address;

    @BindView(R.id.client_area)
    EditText client_area;

    @BindView(R.id.client_city)
    EditText client_city;

    @BindView(R.id.item_no)
    EditText item_no;

    @BindView(R.id.client_order_phone1)
    EditText client_order_phone1;

    @BindView(R.id.status)
    EditText status;

    @BindView(R.id.client_order_phone2)
    EditText client_order_phone2;

    @BindView(R.id.sender_name)
    EditText sender_name;

    @BindView(R.id.item_cost)
    EditText item_cost;

    @BindView(R.id.order_cost)
    EditText order_cost;

    @BindView(R.id.order_total_cost)
    EditText order_total_cost;

    @BindView(R.id.shipping_retum_cost)
    EditText shipping_retum_cost;

    @BindView(R.id.real_shipping_return_cost)
    EditText real_shipping_return_cost;

    @BindView(R.id.client_feedback)
    EditText client_feedback;

    @BindView(R.id.order_type)
    EditText order_type;

    @BindView(R.id.order_view)
    View orderView;


    @BindView(R.id.updating_button)
    MovingButton updatingButton;

    @BindView(R.id.move_listener)
    TextView moveListener;

    @BindView(R.id.no_order_tv)
    TextView noOrder;
    Api api;
    private Order order;
    private String order_ud;
    private String phone;
    ProgressDialog progressDialog;

    public SearchOrderPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_order_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جار تحديث الطلب");

        api = BaseClient.getBaseClient().create(Api.class);
    }

    @OnClick(R.id.search_btn)
    void search() {
        if (phoneInput.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "برجاء ادخال رقم الهاتف", Toast.LENGTH_SHORT).show();
            return;
        }
        api.getPhoneData(
                SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                SharedHelper.getKey(getActivity(), LoginActivity.USER_ID),
                "+201111350338").enqueue(new Callback<UserOrders>() {
            @Override
            public void onResponse(Call<UserOrders> call, Response<UserOrders> response) {
                if (response.body() != null) {
                    //   progressDialog.dismiss();
                    if (response.body().getCode().equals("1202") || response.body().getCode().equals("1200")) {
                        orderView.setVisibility(View.VISIBLE);
                        noOrder.setVisibility(View.GONE);
                        order = response.body().getOrder();
                        // if (order != null) {
                        initUpdateAsNewOrder();
                        order_ud = order.getId();

                        product.setText(order.getProduct_name());
                        status.setText(order.getOrder_status());
                        client_name.setText(order.getClient_name());
                        client_address.setText(order.getClient_address());
                        client_area.setText(order.getClient_area());
                        client_city.setText(order.getClient_city());
                        phone = order.getPhone_1();
                        client_order_phone1.setText(order.getPhone_1());
                        client_order_phone2.setText(order.getPhone_2());
                        item_cost.setText(order.getItem_cost());
                        item_no.setText(order.getItems_no());
                        shipping_retum_cost.setText(order.getShipping_cost());
                        order_cost.setText(order.getOrder_cost());
                        order_total_cost.setText(order.getTotal_order_cost());
                        sender_name.setText(order.getSender_name());
                        order_type.setText(order.getOrder_type());
                        client_feedback.setText(order.getClient_feedback());


                        // } else {
                        //  }

                    } else {
                        orderView.setVisibility(View.GONE);
                        noOrder.setVisibility(View.VISIBLE);

                    }
                    Log.d("eeeeeeeeeeee", "onResponse: " + response.body().getInfo());
                }
            }

            @Override
            public void onFailure(Call<UserOrders> call, Throwable t) {
                Log.d("eeeeeeeeeeeee", "onFailure: " + t.getMessage());
            }
        });

    }



    void initUpdateAsNewOrder() {
        updatingButton.setMovementLeft(300);

        updatingButton.setMovementRight(300);

        updatingButton.setMovementTop(300);

        updatingButton.setMovementBottom(300);

        updatingButton.setOnPositionChangedListener(new MovingButton.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int action, ButtonPosition position) {
                //your code here

                moveListener.setText(setNameNewOrder(position.name()));
            }

            @Override
            public void moveUp(String d) {
                if (d.equals(MovingButton.DOWN)) {
                    updateOrder(client_cancel);
                } else if (d.equals(MovingButton.RIGHT)) {
                    updateOrder(order_data_confirmed);
                } else if (d.equals(MovingButton.LEFT)) {
                    showProblemNoteDialog();
                }
            }
        });

    }

    String setNameNewOrder(String name) {
        if (name.equals(MovingButton.DOWN)) {
            return CANCEL_ORDER_NAME;
        } else if (name.equals(MovingButton.LEFT)) {
            return CLIENT_PROBLEM;
        } else if (name.equals(MovingButton.RIGHT)) {
            return CONFIRM_ORDER_NAME;
        } else
            return "ازاى مفيش";

    }

    void updateOrder(String value) {
        Api api = BaseClient.getBaseClient().create(Api.class);
        api.updateOrders(
                SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                Integer.parseInt(SharedHelper.getKey(getActivity(), LoginActivity.USER_ID)),
                Integer.parseInt(order_ud),
                value
        ).enqueue(new Callback<UpdateOrderResponse>() {
            @Override
            public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                if (response.body() != null) {
                    Log.d("eeeeeeeeeeeeee", "onResponse: updateOrder" + response.body().getCode());
                }
                if (response.body().getCode().equals("1200") || response.body().getCode().equals("1202")) {
                    Log.d("eeeeeeeeeeeeee", "onResponse: updateOrder" + response.body().getCode());
                    progressDialog.dismiss();
                    if (askToFinishWork) {
                        finishTheWorkNow();
                    } else
                        onButtonPressed();
                } else {
                    onButtonPressed();
                }

            }

            @Override
            public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("eeeeeeeeeeeeeeee", "onFailure:update order " + t.getMessage());
                finishTheWorkNow();
            }
        });
    }

    void showProblemNoteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("مشكلة");
        //  alertDialog.setMessage("اكتب المشكلة");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("اكتب المشكلة");
        alertDialog.setView(input);

        alertDialog.setPositiveButton("ارسال",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        if (!input.getText().toString().equals("")) {
                            api.sendProblem(SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                                    Integer.parseInt(SharedHelper.getKey(getActivity(), LoginActivity.USER_ID)),
                                    Integer.parseInt(order.getId()),
                                    input.getText().toString()).enqueue(new Callback<UpdateOrderResponse>() {
                                @Override
                                public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                                    if (response.body() != null) {
                                        if (response.body().getCode().equals("1200")) {

                                            progressDialog.dismiss();
                                            updateOrder(order_data_confirmed);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {

                                }
                            });
                        } else {
                            Toast.makeText(getActivity(),"برجاء ادخال المشكلة", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("الغاء",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
