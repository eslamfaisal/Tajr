package com.greyeg.tajr.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.font.RobotoTextView;
import com.greyeg.tajr.models.SimpleOrderResponse;
import com.greyeg.tajr.models.UpdateOrderResponse;
import com.greyeg.tajr.models.UpdateOrederNewResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.greyeg.tajr.view.ProgressWheel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.greyeg.tajr.activities.OrderActivity.askToFinishWork;
import static com.greyeg.tajr.activities.OrderActivity.finishTheWorkNow;
import static com.greyeg.tajr.activities.OrderActivity.order_data_confirmed;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchOrderPhoneFragment extends Fragment {

    @BindView(R.id.phone_input)
    EditText phoneInput;

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


    @BindView(R.id.move_listener)
    TextView moveListener;

    @BindView(R.id.no_order_tv)
    TextView noOrder;

    @BindView(R.id.progress_search)
    ProgressWheel progressSearch;


    Api api;
    private SimpleOrderResponse.Order order;
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
        progressSearch.spin();

        api = BaseClient.getBaseClient().create(Api.class);


        phoneInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (phoneInput.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "برجاء ادخال رقم الهاتف", Toast.LENGTH_SHORT).show();
                    } else {
                        search();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @OnClick(R.id.search)
    void searchNow() {
        if (phoneInput.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "برجاء ادخال رقم الهاتف", Toast.LENGTH_SHORT).show();
        } else {
            search();
        }

    }

    void search() {
        progressSearch.setVisibility(View.VISIBLE);
        noOrder.setVisibility(View.GONE);
        api.getPhoneData(
                SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                SharedHelper.getKey(getActivity(), LoginActivity.USER_ID),
                phoneInput.getText().toString()

        ).enqueue(new Callback<SimpleOrderResponse>() {
            @Override
            public void onResponse(Call<SimpleOrderResponse> call, Response<SimpleOrderResponse> response) {
                if (response.body() != null) {
                    //   progressDialog.dismiss();
                    if (response.body().getCode().equals("1202") || response.body().getCode().equals("1200")) {
                        orderView.setVisibility(View.VISIBLE);
                        noOrder.setVisibility(View.GONE);
                        progressSearch.setVisibility(View.GONE);
                        order = response.body().getOrder();
                        // if (order != null) {

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

                    } else {
                        orderView.setVisibility(View.GONE);
                        noOrder.setVisibility(View.VISIBLE);
                        progressSearch.setVisibility(View.GONE);

                    }
                    Log.d("eeeeeeeeeeee", "onResponse: " + response.body().getInfo());
                }
            }

            @Override
            public void onFailure(Call<SimpleOrderResponse> call, Throwable t) {
                orderView.setVisibility(View.GONE);
                noOrder.setVisibility(View.VISIBLE);
                progressSearch.setVisibility(View.GONE);

                Log.d("eeeeeeeeeeeee", "onFailure: " + t.getMessage());
            }
        });

    }

    @OnClick(R.id.clear)
    void clearSearch() {
        phoneInput.setText("");
    }


    void updateOrder(String value) {
        Api api = BaseClient.getBaseClient().create(Api.class);
        api.updateOrders(
                SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                order_ud,
                SharedHelper.getKey(getActivity(), LoginActivity.USER_ID),
                value
        ).enqueue(new Callback<UpdateOrederNewResponse>() {
            @Override
            public void onResponse(Call<UpdateOrederNewResponse> call, Response<UpdateOrederNewResponse> response) {
                if (response.body() != null) {
                    Log.d("eeeeeeeeeeeeee", "onResponse: updateOrder" + response.body().getCode());
                }
                if (response.body().getCode().equals("1200") || response.body().getCode().equals("1202")) {
                    Log.d("eeeeeeeeeeeeee", "onResponse: updateOrder" + response.body().getCode());
                    progressDialog.dismiss();
                    if (askToFinishWork) {
                        finishTheWorkNow();
                    } else{
                        //  onButtonPressed();

                    }
                } else {
                   // onButtonPressed();
                }

            }

            @Override
            public void onFailure(Call<UpdateOrederNewResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("eeeeeeeeeeeeeeee", "onFailure:update order " + t.getMessage());
                //finishTheWorkNow();
            }
        });
    }
//
//    void showProblemNoteDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//        alertDialog.setTitle("مشكلة");
//        //  alertDialog.setMessage("اكتب المشكلة");
//
//        final EditText input = new EditText(getActivity());
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        input.setHint("اكتب المشكلة");
//        alertDialog.setView(input);
//
//        alertDialog.setPositiveButton("ارسال",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        progressDialog.show();
//                        if (!input.getText().toString().equals("")) {
//                            api.sendProblem(SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
//                                    Integer.parseInt(SharedHelper.getKey(getActivity(), LoginActivity.USER_ID)),
//                                    Integer.parseInt(order.getId()),
//                                    input.getText().toString()).enqueue(new Callback<UpdateOrderResponse>() {
//                                @Override
//                                public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
//                                    if (response.body() != null) {
//                                        if (response.body().getCode().equals("1200")) {
//
//                                            progressDialog.dismiss();
//                                            updateOrder(order_data_confirmed);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {
//
//                                }
//                            });
//                        } else {
//                            Toast.makeText(getActivity(), "برجاء ادخال المشكلة", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//        alertDialog.setNegativeButton("الغاء",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        alertDialog.show();
//    }

    Dialog problemDialog;
    RobotoTextView sendProblemBtn;
    EditText problemEdt;
    void showProblemNoteDialog() {

        problemDialog = new Dialog(getActivity());
        problemDialog.setContentView(R.layout.dialog_send_problem);
        sendProblemBtn = problemDialog.findViewById(R.id.send);
        problemEdt = problemDialog.findViewById(R.id.problem_Edt);
        sendProblemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("جار ارسال المشكلة");
                progressDialog.show();
                if (!problemEdt.getText().toString().equals("")) {
                    api.sendProblem(SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                            Integer.parseInt(SharedHelper.getKey(getActivity(), LoginActivity.USER_ID)),
                            Integer.parseInt(order.getId()),
                            problemEdt.getText().toString()).enqueue(new Callback<UpdateOrderResponse>() {
                        @Override
                        public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getCode().equals("1200")) {
                                    Toast.makeText(getActivity(), "تم ارسال المشكلة", Toast.LENGTH_SHORT).show();
                                    problemDialog.dismiss();
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
                    Toast.makeText(getActivity(), "برجاء ادخال المشكلة", Toast.LENGTH_SHORT).show();
                }
            }
        });
        problemDialog.show();

    }
//
//    private OnFragmentInteractionListener mListener;
//
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction();
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed() {
//        if (mListener != null) {
//            mListener.onFragmentInteraction();
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
}
