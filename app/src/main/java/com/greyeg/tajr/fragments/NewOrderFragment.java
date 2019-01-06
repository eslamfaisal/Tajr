package com.greyeg.tajr.fragments;

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

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.adapters.ProductSpinnerAdapter;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.ProductData;
import com.greyeg.tajr.models.ProductForSpinner;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    List<String> cities = new ArrayList<>();
    List<String> citiesId = new ArrayList<>();

    public static String CITY_ID;

    private List<Cities.City> citiesBody;

    private void getcities(Api api) {
        if (cities != null && cities.size() > 1) {
            cities.clear();
        }
        Call<Cities> getCiriesCall = api.getCities(
                "F0I+UCx8Bh8nRcIR38/vFD8wDzWyCA3ioaSOdgWdjeIo72ccTTlHFLs2unfLBZwON20wTUM9kzI9LneHiDFM/g==",
                SharedHelper.getKey(getActivity(), LoginActivity.USER_ID)
        );

        getCiriesCall.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                if (response.body() != null) {
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


    private void getProducts(Api api) {
        api.getProducts(SharedHelper.getKey(getActivity(), LoginActivity.TOKEN), Integer.parseInt(SharedHelper.getKey(getActivity(), LoginActivity.USER_ID))
        ).enqueue(new Callback<AllProducts>() {
            @Override
            public void onResponse(Call<AllProducts> call, Response<AllProducts> response) {
                if (response.body() != null) {
                    Log.d("eeeeeeeeeeeeeee", "respons: " + response.body().getCode());
                    List<ProductForSpinner> products = new ArrayList<>();
                    for (ProductData product : response.body().getProducts()) {
                        products.add(new ProductForSpinner(product.getProduct_name(), product.getProduct_image(), product.getProduct_id()));
                    }

                    ArrayAdapter<String> myAdapter = new ProductSpinnerAdapter(
                            getActivity(), products);
                    product.setAdapter(myAdapter);
                }

            }

            @Override
            public void onFailure(Call<AllProducts> call, Throwable t) {
                Log.d("eeeeeeeeeeeeeee", "onFailure: " + t.getMessage());
            }
        });

    }
}
