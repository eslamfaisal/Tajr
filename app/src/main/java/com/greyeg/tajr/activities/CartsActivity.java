package com.greyeg.tajr.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.font.RobotoTextView;
import com.greyeg.tajr.models.CardsResponse;
import com.greyeg.tajr.models.CartResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.greyeg.tajr.view.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CartsActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @BindView(R.id.type)
    Spinner typeSpinner;

    @BindView(R.id.amount)
    Spinner amountSpinner;

    @BindView(R.id.request_cart)
    RobotoTextView requestCart;

    @BindView(R.id.copy)
    RobotoTextView copyCart;


    @BindView(R.id.request_view)
    LinearLayout requestView;

    @BindView(R.id.copy_view)
    LinearLayout copy_view;

    @BindView(R.id.progress_bar)
    ProgressWheel proress;

    @BindView(R.id.cart_num)
    TextView cardText;
    List<String> types = new ArrayList<>();

    List<String> etisalats = new ArrayList<>();
    List<String> vodafone = new ArrayList<>();
    List<String> we = new ArrayList<>();
    List<String> orange = new ArrayList<>();

    List<String> amount = new ArrayList<>();

    private String TYPE = "";
    private String AMOUNT = "";

    String cardNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carts);
        ButterKnife.bind(this);

        Api api = BaseClient.getBaseClient().create(Api.class);
        api.getCarts(SharedHelper.getKey(this, LoginActivity.TOKEN)).enqueue(new Callback<CardsResponse>() {
            @Override
            public void onResponse(Call<CardsResponse> call, Response<CardsResponse> response) {

                etisalats.addAll(response.body().getData().getEtisalat());
                we.addAll(response.body().getData().getWe());
                orange.addAll(response.body().getData().getOrange());
                vodafone.addAll(response.body().getData().getVodafone());

                types.add("vodafone");
                types.add("we");
                types.add("orange");
                types.add("etisalat");

                ArrayAdapter adapter = new ArrayAdapter(CartsActivity.this, android.R.layout.simple_spinner_dropdown_item, types);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(adapter);
                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TYPE = String.valueOf(types.get(position));
                        if (position ==0){
                            amount = vodafone;
                        }else if (position ==1){
                            amount = we;
                        }else if (position ==2){
                            amount = orange;
                        }else if (position ==3){
                            amount = etisalats;
                        }
                        ArrayAdapter adapter = new ArrayAdapter(CartsActivity.this, android.R.layout.simple_spinner_dropdown_item, amount);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        amountSpinner.setAdapter(adapter);
                        amountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                AMOUNT = String.valueOf(amount.get(position));

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<CardsResponse> call, Throwable t) {

            }
        });

        requestCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proress.setVisibility(View.VISIBLE);
                proress.spin();
                requestCart.setVisibility(View.GONE);
                if (TYPE.equals("")||AMOUNT.equals("")){
                    proress.setVisibility(View.GONE);
                    requestCart.setVisibility(View.VISIBLE);
                    Toast.makeText(CartsActivity.this, "برجاء اختيار كل القيم", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    BaseClient.getBaseClient().create(Api.class).getCartDetails(
                            SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN),AMOUNT,TYPE)
                            .enqueue(new Callback<CartResponse>() {
                        @Override
                        public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                            if (response.body().getResponse().equals("success")){
                                copy_view.setVisibility(View.VISIBLE);
                                requestView.setVisibility(View.GONE);
                                cardNum = response.body().getData().getNumber();
                                cardText.setText(cardNum);
                            }

                        }

                        @Override
                        public void onFailure(Call<CartResponse> call, Throwable t) {
                            proress.setVisibility(View.GONE);
                            requestCart.setVisibility(View.VISIBLE);
                        }
                    });

                }
            }
        });

        copyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CartsActivity.this, "تم النسخ", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("card_num", cardNum);
                clipboard.setPrimaryClip(clip);

            }
        });
    }


    void copy(String number){

    }

}
