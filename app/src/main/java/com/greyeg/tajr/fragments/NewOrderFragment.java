package com.greyeg.tajr.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.adapters.ProductSpinnerAdapter;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.ProductData;
import com.greyeg.tajr.models.ProductForSpinner;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.greyeg.tajr.activities.OrderActivity.currentClientID;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewOrderFragment extends Fragment {


    @BindView(R.id.product)
    Spinner product;

    @BindView(R.id.client_name)
    EditText client_name;

    @BindView(R.id.client_address)
    EditText client_address;

    @BindView(R.id.client_area)
    EditText client_area;

    @BindView(R.id.client_city)
    Spinner client_city;

    @BindView(R.id.item_no)
    EditText item_no;

    @BindView(R.id.client_order_phone1)
    EditText client_order_phone1;

    View mainView;

    String productId;

    List<ProductForSpinner> products;

    List<String> cities = new ArrayList<>();
    List<String> citiesId = new ArrayList<>();

    public static String CITY_ID;

    private List<Cities.City> citiesBody;

    public NewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_new_order, container, false);

        ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Api api = BaseClient.getBaseClient().create(Api.class);

        getProducts(api);
        getcities(api);

    }

    private void getcities(Api api) {
        if (cities != null && cities.size() > 1) {
            cities.clear();
        }
        Call<Cities> getCiriesCall = api.getCities(
                SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                SharedHelper.getKey(getActivity(), LoginActivity.PARENT_ID)
        );

        getCiriesCall.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                if (response.body().getCities() != null) {
                    if (response.body().getCities().size() > 0) {

                        citiesBody = response.body().getCities();
                        for (Cities.City city : citiesBody) {
                            cities.add(city.getCity_name());
                            citiesId.add(city.getCity_id());
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.layout_cities_spinner_item, cities);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        client_city.setAdapter(adapter);
                        client_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                CITY_ID = String.valueOf(citiesBody.get(position).getCity_id());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {

            }
        });
    }

  public static AllProducts allProducts;

    private void getProducts(Api api) {
        api.getProducts(SharedHelper.getKey(getActivity(), LoginActivity.TOKEN),
                currentClientID
        ).enqueue(new Callback<AllProducts>() {
            @Override
            public void onResponse(Call<AllProducts> call, final Response<AllProducts> response) {
                Log.d("eeeeeeeeeeeeeee", "respons: " + response.body().getProducts_count());
                if (response.body() != null&&response.body().getProducts()!=null) {
                    allProducts = response.body();
                    if (response.body().getProducts().size() > 0) {
                        productId = response.body().getProducts().get(0).getProduct_id();

                    }
                    products = new ArrayList<>();
                    for (ProductData product : response.body().getProducts()) {
                        products.add(new ProductForSpinner(product.getProduct_name(), product.getProduct_image(), product.getProduct_id(),product.getProduct_real_price()));
                    }
                    ArrayAdapter<String> myAdapter = new ProductSpinnerAdapter(
                            getActivity(), products);
                    product.setAdapter(myAdapter);

                    product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            productId = response.body().getProducts().get(position).getProduct_id();
                            // Toast.makeText(getActivity(), ""+productId, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AllProducts> call, Throwable t) {
                Log.d("eeeeeeeeeeeeeee", "onFailure: " + t.getMessage());
            }
        });

    }

    ProgressDialog progressDialog;
    @OnClick(R.id.send_order)
    void sendOrder() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("جار ارسال الطلب");
        progressDialog.show();
        if (productId != null && CITY_ID != null &&
                !client_name.getText().toString().equals("")
                && !client_address.getText().toString().equals("")
                && !client_area.getText().toString().equals("")
                && !client_order_phone1.getText().toString().equals("")
                && !item_no.getText().toString().equals("")
                ) {
            Api api = BaseClient.getBaseClient().create(Api.class);
            api.recordNewOrder(
                    SharedHelper.getKey(getActivity(),LoginActivity.TOKEN),
                    currentClientID,
                    productId,
                    client_name.getText().toString(),
                    client_order_phone1.getText().toString(),
                    CITY_ID,
                    client_area.getText().toString(),
                    client_address.getText().toString(),
                    item_no.getText().toString()
            ).enqueue(new Callback<NewOrderResponse>() {
                @Override
                public void onResponse(Call<NewOrderResponse> call, Response<NewOrderResponse> response) {
                    progressDialog.dismiss();
                    Log.d("GGGGGGGGGGgggggg", "onResponse: "+response.body().getCode());
                    if (response.body().getCode().equals("1411")){
                        showDialog(getString(R.string.invalid_ph_num));
                    }else if (response.body().getCode().equals("1200")){
                        Toast.makeText(getActivity(), getString(R.string.added_success), Toast.LENGTH_SHORT).show();
                   //     onButtonPressed();
                    }else {
                        showDialog(response.body().getDetails());
                    }
                }

                @Override
                public void onFailure(Call<NewOrderResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.d("GGGGGGGGGGgggggg", "onFailure: "+t.getMessage());
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "برجاء ادخال جميع البيانات", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_warning);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
//
//    private SendOrderListener mListener;
//
//    public interface SendOrderListener {
//        // TODO: Update argument type and name
//        void orderSentGetNewOrder();
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed() {
//        if (mListener != null) {
//            mListener.orderSentGetNewOrder();
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof SendOrderListener) {
//            mListener = (SendOrderListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    };
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

}
