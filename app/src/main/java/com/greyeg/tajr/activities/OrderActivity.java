package com.greyeg.tajr.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.greyeg.tajr.R;
import com.greyeg.tajr.call_receiver.PhoneCallReceiver;
import com.greyeg.tajr.helper.CurrentCallListener;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.TimerTextView;
import com.greyeg.tajr.models.Order;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.greyeg.tajr.services.TimerServices;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.greyeg.tajr.activities.LoginActivity.IS_LOGIN;

public class OrderActivity extends AppCompatActivity implements CurrentCallListener {

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

//    @BindView(R.id.update)
//    DrawMeButton update;
//
//    @BindView(R.id.cancel)
//    DrawMeButton cancel;

    public static TimerTextView timerTextView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String phone;
    private String order_ud = "idid";

    long startWorkTime;

    public static Activity my;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);
        ButterKnife.bind(this);
        PhoneCallReceiver.setCurrentCallListener(this);
        my = this;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        timerTextView = findViewById(R.id.timer);
        startService(new Intent(this, TimerServices.class));
        TimerServices.stoped = false;
        startTimer(System.currentTimeMillis() - (TimerServices.timeWork * 1000));
        getFirstOrder();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Log.d("laaaaaaaaaaast", "onCreate: " + getLastCallDetails(this));
        // Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();



    }

    boolean working = false;

    public void startTimer(long start) {
        working = true;
        timerTextView.setStarterTime(start);
        timerTextView.startTimer();
    }


    public static void pauseTimer() {

        TimerServices.stoped = true;
        timerTextView.stopTimer();
        my.invalidateOptionsMenu();
    }

    void resumeTimer() {
        askToFinishWork = false;
        TimerServices.stoped = false;
        startTimer(System.currentTimeMillis() - (TimerServices.timeWork * 1000));
        my.invalidateOptionsMenu();
    }

    private void getFirstOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جار جلب الطلب");
        progressDialog.show();
        api = BaseClient.getBaseClient().create(Api.class);

        api.getOrders(SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID)).enqueue(new Callback<UserOrders>() {
            @Override
            public void onResponse(Call<UserOrders> call, Response<UserOrders> response) {
                if (response.body() != null) {
                    progressDialog.dismiss();
                    if (response.body().getCode().equals("1202") || response.body().getCode().equals("1200")) {
                        UserOrders orders = response.body();
                        Order order = orders.getOrder();
                        if (order != null) {
                            if (!order_ud.equals(order.getId())) {
                                //  Toast.makeText(OrderActivity.this, "طلب جديد", Toast.LENGTH_SHORT).show();
                            }
                            order_ud = order.getId();

                            product.setText(order.getProduct_name());
                            status.setText(order.getOrder_status());
                            client_name.setText(order.getClient_name());
                            client_address.setText(order.getClient_address());
                            client_area.setText(order.getClient_area());
                            client_city.setText(order.getClient_city());
                            PhoneCallReceiver.myCallNumber = phone = order.getPhone_1();
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

                            if (!TimerServices.stoped) ;
                            //   MakePhoneCall();

                        } else {
                        }

                    } else {
                        SharedHelper.putKey(getApplicationContext(), IS_LOGIN, "no");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                    }
                    Log.d("eeeeeeeee", "onResponse: " + response.body().getInfo());
                }
            }

            @Override
            public void onFailure(Call<UserOrders> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void MakePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + "01022369592"));
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1232);
            }
        } else {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1232) {
            MakePhoneCall();
        }
    }

//    @OnClick(R.id.update)
//    void updateOrder(){
//        Api api = BaseClient.getBaseClient().create(Api.class);
//        api.updateOrders(
//                SharedHelper.getKey(this,LoginActivity.TOKEN),
//                Integer.parseInt(SharedHelper.getKey(this,LoginActivity.USER_ID)),
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
                //  Toast.makeText(OrderActivity.this, newStatusTag, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    void home() {
        /* This should come from a preference that let's the user select an activity that can handle the HOME intent */
        String packageName = "com.android.launcher";
        String packageClass = "com.android.launcher2.Launcher";

        Intent home_intent = new Intent(Intent.ACTION_MAIN);
        home_intent.addCategory(Intent.CATEGORY_HOME);
        home_intent.setComponent(new ComponentName(packageName, packageClass));
        home_intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        /* Here you should catch the exception when the launcher has been uninstalled, and let the user save themselves by opening the Market or an app list or something. Users sometimes use root apps to uninstall the system launcher, so your fake launcher is all that is left. Might as well give the poor user a hand. */
        startActivity(home_intent);
    }

    Timer pauseActivityTimer;

    long pauseActivityTimerCount;

    @Override
    protected void onPause() {
        super.onPause();
        if (!finish){
            pauseActivityTimer = new Timer();
            pauseActivityTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    pauseActivityTimerCount+=1;
                    if (pauseActivityTimerCount==5){
                        pauseActivityTimerCount=0;
                        Intent intent = new Intent(getApplicationContext(),EmptyCallActivity.class);
                        startActivity(intent);
                    }
                }
            },0,1000);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        finish=false;
        if (pauseActivityTimer!=null)
        {
            pauseActivityTimer.cancel();
            pauseActivityTimer =null;
        }

    }

    MenuItem askToFinishWortkItem;
    MenuItem finishWok;
    MenuItem micMode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);

        askToFinishWortkItem = menu.findItem(R.id.ask_finish_work);
        finishWok = menu.findItem(R.id.finish_work);
        micMode = menu.findItem(R.id.mic_mode);
        if (askToFinishWork) {
            finishWok.setVisible(true);
            askToFinishWortkItem.setVisible(false);
        } else {
            finishWok.setVisible(false);
            askToFinishWortkItem.setVisible(true);
        }

        if (micOff) {
            micMode.setIcon(R.drawable.ic_mic_none_black_24dp);
            micMode.setTitle("تشغل الميك");

        } else {
            micMode.setIcon(R.drawable.ic_mic_off_black_24dp);
            micMode.setTitle("ايقاف الميك");
        }


        return true;
    }

    boolean micOff = false;

    boolean finish=false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.finish_work) {
            finish=true;
            finish();

        } else if (id == R.id.end_call) {
            endCAll();
        } else if (id == R.id.call_client) {
            MakePhoneCall();
        } else if (id == R.id.ask_finish_work) {
            askToFinishWork = true;
            invalidateOptionsMenu();
        } else if (id == R.id.mic_mode) {
            modifyMic();
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    void modifyMic() {

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (!audioManager.isMicrophoneMute()) {
            micOff = false;
            audioManager.setMicrophoneMute(true);

        } else {
            micOff = true;
            audioManager.setMicrophoneMute(false);

        }
    }

    public static String getLastCallDetails(Context context) {

        //CallDetails callDetails = new CallDetails();

        String ty = null;
        Uri contacts = CallLog.Calls.CONTENT_URI;
        try {

            Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int incomingtype = managedCursor.getColumnIndex(String.valueOf(CallLog.Calls.INCOMING_TYPE));

            if (managedCursor.moveToFirst()) { // added line
                //  managedCursor.moveToFirst();
                while (managedCursor.moveToNext()) {
                    String callType;
                    String phNumber = managedCursor.getString(number);
                    // String callerName = getContactName(context, phNumber);
                    if (incomingtype == -1) {
                        callType = "incoming";
                    } else {
                        callType = "outgoing";
                    }

                    String callDate = managedCursor.getString(date);
                    String callDayTime = new Date(Long.valueOf(callDate)).toString();
                    String callDuration = managedCursor.getString(duration);
                    ty = phNumber;

//                    callDetails.setCallerName(callerName);
//                    callDetails.setCallerNumber(phNumber);
//                    callDetails.setCallDuration(callDuration);
//                    callDetails.setCallType(callType);
//                    callDetails.setCallTimeStamp(callDayTime);

                }
            }

            managedCursor.close();

        } catch (SecurityException e) {
            Log.e("eeeeeeeeeeeeeeeeeeeee", "User denied call log permission");

        }

        return ty;

    }

    void endCAll() {
        boolean success = false;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Class clazz = null;
        try {
            clazz = Class.forName(telephonyManager.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method method = null;
        try {
            method = clazz.getDeclaredMethod("getITelephony");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        method.setAccessible(true);
        ITelephony telephonyService = null;
        try {
            telephonyService = (ITelephony) method.invoke(telephonyManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        telephonyService.endCall();

    }

    public static boolean askToFinishWork = false;
    boolean askToPauseWork;

    void setAskToFinishWork() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pause_work);
        builder.setMessage(R.string.pause_work_message);
        builder.setPositiveButton(R.string.finish_work, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (askToFinishWork && TimerServices.stoped)
                    finish();
                else
                    askToFinishWork = true;
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("dddddddddd", "onDestroy: " + "finised");
        TimerServices.stoped = false;

        api.userWorkTime(SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID), String.valueOf(TimerServices.timeWork))
                .enqueue(new Callback<UserWorkTimeResponse>() {
                    @Override
                    public void onResponse(Call<UserWorkTimeResponse> call, Response<UserWorkTimeResponse> response) {
                        if (response.body() != null) {
                            //  Toast.makeText(OrderActivity.this, "اشتغلت " + TimerServices.timeWork, Toast.LENGTH_SHORT).show();
                            Log.d("dddddddddd", "distroy: " + response.body().getCode());
                            Log.d("dddddddddd", "distroy: " + response.body().getInfo());
                            TimerServices.timeWork = 0;
                            stopService(new Intent(getApplicationContext(), TimerServices.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserWorkTimeResponse> call, Throwable t) {

                    }
                });

    }


    String lastCall() {


        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";


        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        String callDuration = null;
        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);

            long seconds = managedCursor.getLong(date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy  hh:mm:ss a");
            String dateString = formatter.format(new java.sql.Date(seconds));

            String strcallDate = managedCursor.getString(date);
            java.sql.Date callDate = new java.sql.Date(Long.valueOf(strcallDate));

            callDuration = managedCursor.getString(duration);
            String callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    callType = "";
                    break;
            }


        }
        managedCursor.close();
        return callDuration;
    }



    @Override
    public void callEnded() {
        Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
    }
}

