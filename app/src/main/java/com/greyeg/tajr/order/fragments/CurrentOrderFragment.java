package com.greyeg.tajr.order.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.order.NewOrderActivity;
import com.greyeg.tajr.order.enums.ResponseCodeEnums;
import com.greyeg.tajr.order.models.City;
import com.greyeg.tajr.order.models.CurrentOrderResponse;
import com.greyeg.tajr.order.models.Order;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.greyeg.tajr.view.dialogs.Dialogs;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.greyeg.tajr.activities.LoginActivity.IS_LOGIN;
import static com.greyeg.tajr.view.dialogs.Dialogs.showProgressDialog;

public class CurrentOrderFragment extends Fragment {

    private final String TAG = "CurrentOrderFragment";

    // main view of the CurrentOrderFragment
    View mainView;

    @BindView(R.id.client_name)
    EditText client_name;

    @BindView(R.id.client_address)
    EditText client_address;

    @BindView(R.id.client_area)
    EditText client_area;

    @BindView(R.id.item_no)
    EditText item_no;

    @BindView(R.id.client_order_phone1)
    EditText client_order_phone1;

    @BindView(R.id.status)
    EditText status;

    @BindView(R.id.shipping_status)
    EditText shipping_status;

    @BindView(R.id.shipping_cost)
    EditText shipping_cost;

    @BindView(R.id.sender_name)
    EditText sender_name;

    @BindView(R.id.item_cost)
    EditText item_cost;

    @BindView(R.id.ntes)
    EditText notes;

    @BindView(R.id.products_recycler_view)
    RecyclerView productsRecyclerView;

    @BindView(R.id.discount)
    EditText discount;

    @BindView(R.id.order_total_cost)
    EditText order_total_cost;

    @BindView(R.id.client_feedback)
    EditText client_feedback;

    @BindView(R.id.order_id)
    EditText order_id;

    @BindView(R.id.client_city)
    Spinner client_city;

    @BindView(R.id.add_product)
    ImageView add_product;

    @BindView(R.id.order_type)
    EditText order_type;

    public CurrentOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_current_order, container, false);
        ButterKnife.bind(this, mainView);

        initLabels();
        getCurrentOrder();
        return mainView;
    }

    private void getCurrentOrder() {

        ProgressDialog progressDialog = showProgressDialog(getActivity(),getString(R.string.fetching_th_order));
        BaseClient.getBaseClient().create(Api.class)
                .getNewCurrentOrderResponce(SharedHelper.getKey(getActivity(), LoginActivity.TOKEN))
                .enqueue(new Callback<CurrentOrderResponse>() {
                    @Override
                    public void onResponse(Call<CurrentOrderResponse> call, Response<CurrentOrderResponse> response) {
                        progressDialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().getCode().equals(ResponseCodeEnums.code_1200.getCode())) {
                                NewOrderActivity.currentOrderResponse = response.body();
                                fillFieldsWithOrderData(response.body());
                            } else if (response.body().getCode().equals(ResponseCodeEnums.code_1300.getCode())) {
                                // no new orders all handled
                                //TODO make dialog with return
                                Dialogs.showCustomDialog(getActivity(), getString(R.string.no_more_orders), getString(R.string.order),
                                        "Back", null, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                NewOrderActivity.finishWork();
                                            }
                                        }, null);
                            } else if (
                                    response.body().getCode().equals(ResponseCodeEnums.code_1407.getCode()) ||
                                            response.body().getCode().equals(ResponseCodeEnums.code_1408.getCode()) ||
                                            response.body().getCode().equals(ResponseCodeEnums.code_1490.getCode()) ||
                                            response.body().getCode().equals(ResponseCodeEnums.code_1511.getCode()) ||
                                            response.body().getCode().equals(ResponseCodeEnums.code_1440.getCode())) {

                                SharedHelper.putKey(getActivity(), IS_LOGIN, "no");
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                NewOrderActivity.finishWork();
                            }

                        } else {
                            Dialogs.showCustomDialog(getActivity(),
                                    response.toString(), getString(R.string.order),
                                    getString(R.string.retry), getString(R.string.finish_work),
                                    v -> getActivity().recreate(),
                                    v -> NewOrderActivity.finishWork());
                            //TODO maje dialog
                            Log.d(TAG, "onResponse: null = " + response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentOrderResponse> call, Throwable t) {
                        Dialogs.showCustomDialog(getActivity(),
                                t.getMessage(), getString(R.string.order),
                                getString(R.string.retry), getString(R.string.finish_work),
                                v -> getActivity().recreate(),
                                v -> NewOrderActivity.finishWork());
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }

    private void fillFieldsWithOrderData(CurrentOrderResponse orderResponse) {

        Order order = orderResponse.getOrder();
        status.setText(order.getOrderStatus());
        client_name.setText(order.getClientName());
        client_address.setText(order.getClientAddress());
        client_area.setText(order.getClientArea());
        shipping_status.setText(order.getOrderShippingStatus());
        client_order_phone1.setText(order.getPhone1());
        order_id.setText(order.getId());
        item_cost.setText(order.getItemCost());
        item_no.setText(order.getItemsNo());
        order_total_cost.setText(order.getTotalOrderCost());
        sender_name.setText(order.getSenderName());
        order_type.setText(order.getOrderType());
        client_feedback.setText(order.getClientFeedback());
        notes.setText(order.getNotes());
        discount.setText(order.getDiscount());
        shipping_cost.setText(order.getShippingCost());

        initCities(orderResponse);
    }

    private void initCities(CurrentOrderResponse order) {

        ArrayList<String> citiesNames = new ArrayList<>();
        for (City city : order.getCities()) {
            citiesNames.add(city.getCityName());
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.layout_cities_spinner_item, citiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        client_city.setAdapter(adapter);
        int cityIndex = citiesNames.indexOf(order.getOrder().getClientCity());
        if (cityIndex < 0) {
            cityIndex = 0;
        }
        client_city.setSelection(cityIndex);
        client_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: "+position);
                Log.d(TAG, "onItemSelected: "+client_city.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // init hide and show labels
    private void initLabels() {
        LinearLayout linearLayout = mainView.findViewById(R.id.order_fields);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            LinearLayout chiledLinearLayout = linearLayout.findViewById(linearLayout.getChildAt(i).getId());
            if (chiledLinearLayout != null)
                chiledLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runAnimation(chiledLinearLayout.getChildAt(0).getId(), chiledLinearLayout.getChildAt(1).getId());
                    }
                });
        }
    }

    // animation for show and hide fields labels
    private void runAnimation(int id1, int id2) {

        TextView tv = (TextView) mainView.findViewById(id1);
        FrameLayout bg = (FrameLayout) mainView.findViewById(id2);

        if (tv.getVisibility() == View.VISIBLE) {
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_sheet_fad_out);
            a.reset();
            tv.clearAnimation();
            tv.startAnimation(a);
            tv.setVisibility(View.GONE);
            bg.setBackgroundResource(R.drawable.ic_background_gray);
        } else {
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_sheet_fad_in);
            a.reset();
            tv.clearAnimation();
            tv.startAnimation(a);
            tv.setVisibility(View.VISIBLE);
            bg.setBackgroundResource(R.drawable.ic_background_gray_down);

        }

    }
    

}
