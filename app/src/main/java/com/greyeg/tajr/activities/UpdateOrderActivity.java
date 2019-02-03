package com.greyeg.tajr.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.TimerTextView;
import com.greyeg.tajr.models.Order;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.rafakob.drawme.DrawMeButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

import static com.greyeg.tajr.activities.LoginActivity.IS_LOGIN;

public class UpdateOrderActivity extends AppCompatActivity {

    @BindView(R.id.product)
    EditText product;

    @BindView(R.id.status)
    EditText status;

    @BindView(R.id.client_name)
    EditText client_name;

    @BindView(R.id.client_address)
    EditText client_address;

    @BindView(R.id.client_area)
    EditText client_area;

    @BindView(R.id.client_city)
    EditText client_city;

    @BindView(R.id.client_order_phone1)
    EditText client_order_phone1;

    @BindView(R.id.client_order_phone2)
    EditText client_order_phone2;

    @BindView(R.id.sender_name)
    EditText sender_name;

    @BindView(R.id.item_cost)
    EditText item_cost;

    @BindView(R.id.item_no)
    EditText item_no;

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

    @BindView(R.id.update)
    DrawMeButton update;
//
//    @BindView(R.id.cancel)
//    DrawMeButton cancel;

    @BindView(R.id.timer)
    TimerTextView timerTextView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String phone;
    private String order_ud = "idid";
    boolean first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        setContentView(R.layout.activity_update_order2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        if (MainActivity.startTime != 0) {
//            timerTextView.setStarterTime(MainActivity.startTime);
//            timerTextView.startTimer();
//        }

        getFirstOrder();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //      PhoneListener phoneListener = PhoneListener.getInstance(getApplicationContext());


    }

    Order order =null;
    private void getFirstOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جار جلب الطلب");
        progressDialog.show();
        Api api = BaseClient.getBaseClient().create(Api.class);

        api.getOrders("Z7CQVAoJXzu2iv5dgMIBUk36").enqueue(new Callback<UserOrders>() {
            @Override
            public void onResponse(retrofit2.Call<UserOrders> call, Response<UserOrders> response) {
                if (response.body() != null) {
                    progressDialog.dismiss();
                    if (response.body().getCode().equals("1202") || response.body().getCode().equals("1200")) {
                        UserOrders orders = response.body();
                        order = orders.getOrder();
                        if (order != null) {
                            if (first) finish();
                            else first = true;

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

                            //callClient();

                        } else {
                        }

                    } else {
                        SharedHelper.putKey(getApplicationContext(), IS_LOGIN, "no");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                    Log.d("eeeeeeeee", "onResponse: " + response.body().getInfo());
                }
            }

            @Override
            public void onFailure(Call<UserOrders> call, Throwable t) {
                Log.d("eeeeeeeee", "onResponse: " + t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    public void MakePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + "01023619800"));
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1232);
            }
        } else
            startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1232) {
            //callClient();
        }
    }

//    @OnClick(R.id.update)
//    void updateOrder() {
//        Api api = BaseClient.getBaseClient().create(Api.class);
//        api.updateOrders(
//                SharedHelper.getKey(this, LoginActivity.TOKEN),
//                Integer.parseInt(SharedHelper.getKey(this, LoginActivity.USER_ID)),
//                Integer.parseInt(order_ud),
//                newStatusTag
//        ).enqueue(new Callback<UserOrders>() {
//            @Override
//            public void onResponse(Call<UserOrders> call, Response<UserOrders> response) {
//                getFirstOrder();
//            }
//
//            @Override
//            public void onFailure(Call<UserOrders> call, Throwable t) {
//                getFirstOrder();
//            }
//        });
//    }

    RadioGroup radioGroup;
    Button ok;
    View view;
    String newStatus;
    String newStatusTag;

    @OnClick(R.id.status)
    void showResponceToUpdate() {
        final Dialog dialog = new Dialog(this);
        view = LayoutInflater.from(this).inflate(R.layout.layout_order_status_list, null);
        dialog.setContentView(view);
        radioGroup = view.findViewById(R.id.radio_group);

        ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selectedRadioButton = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
                newStatus = selectedRadioButton.getText().toString();
                newStatusTag = (String) selectedRadioButton.getTag();
                status.setText(newStatus);
                Toast.makeText(UpdateOrderActivity.this, newStatusTag, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    void initRadio() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.call_client) {
//            TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//            Class clazz = null;
//            try {
//                clazz = Class.forName(telephonyManager.getClass().getName());
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            Method method = null;
//            try {
//                method = clazz.getDeclaredMethod("getITelephony");
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            method.setAccessible(true);
//            ITelephony telephonyService = null;
//            try {
//                telephonyService = (ITelephony) method.invoke(telephonyManager);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//            telephonyService.endCall();
        }
        return super.onOptionsItemSelected(item);
    }
//
//    String number;
//    void stat(){
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        PhoneStateListener callStateListener = new PhoneStateListener() {
//
//            public void onCallStateChanged(int state, String incomingNumber) {
//                // TODO React to a incoming call.
//
//                try {
//                    if (state == TelephonyManager.CALL_STATE_RINGING) {
//                        Toast.makeText(getApplicationContext(), "Phone Is Ringing" + incomingNumber, Toast.LENGTH_LONG).show();
//                        number = incomingNumber;
//                        //g.setPhoneNo(incomingNumber);
//                     //   AndroidNetCommunicationClientActivity.mMsgSendRequest("CommandMsgCallIncoming" + number);
//                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//                        Toast.makeText(getApplicationContext(), "Phone is Currently in A call" + incomingNumber, Toast.LENGTH_LONG).show();
//                        //number = mIntent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                        number = incomingNumber;
//
//
//                    } else if (state == TelephonyManager.DATA_DISCONNECTED) {
//                        number = "";
//                        //CallID = "";
//                    }
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    //conn.MessageBox(MainActivity.this, e.getMessage());
//                    e.printStackTrace();
//                }
//                super.onCallStateChanged(state, incomingNumber);
//
//            }
//        };
//        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
//    }
}
