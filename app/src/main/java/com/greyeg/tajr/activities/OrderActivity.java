package com.greyeg.tajr.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.greyeg.tajr.R;
import com.greyeg.tajr.calc.CalcDialog;
import com.greyeg.tajr.call_receiver.PhoneCallReceiver;
import com.greyeg.tajr.fragments.NewOrderFragment;
import com.greyeg.tajr.fragments.SearchOrderPhoneFragment;
import com.greyeg.tajr.helper.CurrentCallListener;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.TimerTextView;
import com.greyeg.tajr.helper.font.RobotoTextView;
import com.greyeg.tajr.models.LastCallDetails;
import com.greyeg.tajr.models.Order;
import com.greyeg.tajr.models.UpdateOrderResponse;
import com.greyeg.tajr.models.UserOrders;
import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.thefinestartist.movingbutton.MovingButton;
import com.thefinestartist.movingbutton.enums.ButtonPosition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.greyeg.tajr.activities.LoginActivity.IS_LOGIN;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.calc.CalcDialog;

import java.math.BigDecimal;
public class OrderActivity extends AppCompatActivity implements CurrentCallListener,
        SearchOrderPhoneFragment.OnFragmentInteractionListener, NewOrderFragment.SendOrderListener , CalcDialog.CalcDialogCallback  {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private static final String TAG = CalcActivity.class.getSimpleName();

    private static final int DIALOG_REQUEST_CODE = 0;

    private TextView valueTxv;
    private CheckBox signChk;

    private @Nullable
    BigDecimal value;

    public static final String client_busy = "client_busy";
    public static final String CLIENT_BUSY_NAME = "العميل مشغول";

    public static final String client_delay = "client_delay";

    public static final String client_cancel = "client_cancel";
    public static final String CANCEL_ORDER_NAME = "الغاء الطلب";
    public static final String client_noanswer = "client_noanswer";

    public static final String order_data_confirmed = "order_data_confirmed";
    public static final String CONFIRM_ORDER_NAME = "تاكيد الطلب";
    public static final String client_phone_error = "client_phone_error";
    public static final String WRONG_PHONE_NAME = "رقم هاتف خاطئ";

    public static final String CLIENT_PROBLEM = "مشكلة";
    public static final String RECHARGE = "اعادة شحن";


    @BindView(R.id.product)
    TextView product;

    @BindView(R.id.client_name)
    TextView client_name;

    @BindView(R.id.client_address)
    TextView client_address;

    @BindView(R.id.client_area)
    TextView client_area;

    @BindView(R.id.client_city)
    TextView client_city;

    @BindView(R.id.item_no)
    TextView item_no;

    @BindView(R.id.client_order_phone1)
    TextView client_order_phone1;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.shipping_status)
    TextView shipping_status;

    @BindView(R.id.sender_name)
    TextView sender_name;

    @BindView(R.id.item_cost)
    TextView item_cost;

    @BindView(R.id.discount)
    TextView discount;

    @BindView(R.id.order_cost)
    TextView order_cost;

    @BindView(R.id.order_total_cost)
    TextView order_total_cost;

    @BindView(R.id.client_feedback)
    TextView client_feedback;

//    @BindView(R.id.update)
//    DrawMeButton update;
//
//    @BindView(R.id.cancel)
//    DrawMeButton cancel;

    public static TimerTextView timerTextView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public static String phone;
    private String order_ud = null;

    long startWorkTime;

    public static Activity my;
    private Api api;

    @BindView(R.id.updating_button)
    MovingButton updatingButton;

    RadioGroup radioGroup;
    Button ok;
    View view;
    String newStatus;
    String newStatusTag;


    Timer pauseActivityTimer;

    long pauseActivityTimerCount;
    ProgressDialog progressDialog;

    boolean micMute = false;
    public static long timeWork;
    public static boolean finish = false;
    public static boolean askToFinishWork = false;

    @BindView(R.id.order_view)
    View orederView;

    @BindView(R.id.missed_call_view)
    View missed_call_view;

    public static Activity orderActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);
        ButterKnife.bind(this);
        setCalc(savedInstanceState);
        registerBroadCastReceiver();
        orderActivity = this;
        PhoneCallReceiver.setCurrentCallListener(this);
        my = this;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        timerTextView = findViewById(R.id.timer);
        timeWork = 0;
        startWorkTimerTimer();
        stoped = false;
        startTimer(System.currentTimeMillis() - (timeWork * 1000));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        api = BaseClient.getBaseClient().create(Api.class);

        micMute = false;
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
        }
        finish = false;
        askToFinishWork = false;
        invalidateOptionsMenu();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.updating_order));

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(this.getString(R.string.new_order), NewOrderFragment.class)
                .add(this.getString(R.string.search), SearchOrderPhoneFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        naveMenu = navigation.getMenu();

        micMode = naveMenu.findItem(R.id.mic_mode);

        if (!micMute) {
            micMode.setIcon(R.drawable.ic_mic_none_black_24dp);
            micMode.setTitle(getString(R.string.mute_mic));

        } else {
            micMode.setIcon(R.drawable.ic_mic_off_black_24dp);
            micMode.setTitle(getString(R.string.turn_on_mic));
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getFirstOrder();
        createWarningDialog();
        Log.d("dddddddddd", "time started: " + timeWork);
    }

    Menu naveMenu;

    @BindView(R.id.move_listener)
    TextView moveListener;

    void initUpdatingButton() {
        updatingButton.setMovementLeft(300);

        updatingButton.setMovementRight(300);

        updatingButton.setMovementTop(300);

        updatingButton.setMovementBottom(300);

        updatingButton.setOnPositionChangedListener(new MovingButton.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int action, ButtonPosition position) {
                //your code here

                moveListener.setText(position.name());
            }

            @Override
            public void moveUp(String position) {
                Toast.makeText(OrderActivity.this, position, Toast.LENGTH_SHORT).show();
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

                if (d.equals(MovingButton.UP)) {
                    updateOrder(client_phone_error);
                } else if (d.equals(MovingButton.DOWN)) {
                    updateOrder(client_busy);
                } else if (d.equals(MovingButton.RIGHT)) {
                    updateOrder(order_data_confirmed);
                } else if (d.equals(MovingButton.LEFT)) {
                    updateOrder(client_cancel);
                }
            }
        });

    }

    void initUpdateAsOldOrder() {

        updatingButton.setMovementLeft(300);

        updatingButton.setMovementRight(300);

        updatingButton.setMovementTop(300);

        updatingButton.setMovementBottom(300);

        updatingButton.setOnPositionChangedListener(new MovingButton.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int action, ButtonPosition position) {
                //your code here

                moveListener.setText(setNameOldOrder(position.name()));
            }

            @Override
            public void moveUp(String d) {

                if (d.equals(MovingButton.UP)) {
                    updateOrder(client_phone_error);
                } else if (d.equals(MovingButton.DOWN)) {
                    updateOrder(client_busy);
                } else if (d.equals(MovingButton.RIGHT)) {
                    updateOrder(order_data_confirmed);
                } else if (d.equals(MovingButton.LEFT)) {
                    showProblemNoteDialog();
                }
            }
        });

    }

    String setNameNewOrder(String name) {
        if (name.equals(MovingButton.UP)) {
            return WRONG_PHONE_NAME;
        } else if (name.equals(MovingButton.DOWN)) {
            return CLIENT_BUSY_NAME;
        } else if (name.equals(MovingButton.LEFT)) {
            return CANCEL_ORDER_NAME;
        } else if (name.equals(MovingButton.RIGHT)) {
            return CONFIRM_ORDER_NAME;
        } else
            return "ازاى مفيش";

    }

    String setNameOldOrder(String name) {
        if (name.equals(MovingButton.UP)) {
            return RECHARGE;
        } else if (name.equals(MovingButton.DOWN)) {
            return CLIENT_BUSY_NAME;
        } else if (name.equals(MovingButton.LEFT)) {
            return CLIENT_PROBLEM;
        } else if (name.equals(MovingButton.RIGHT)) {
            return CONFIRM_ORDER_NAME;
        } else
            return "ازاى مفيش";

    }


    void updateOrder(String value) {
        if (value == null || order_ud == null) {
            return;
        }
        if (updateOrderTimer != null) {
            updateOrderTimer.cancel();
            updateOrderTimerCount = 0;
        }

        Api api = BaseClient.getBaseClient().create(Api.class);
        api.updateOrders(
                SharedHelper.getKey(this, LoginActivity.TOKEN),
                Integer.parseInt(SharedHelper.getKey(this, LoginActivity.USER_ID)),
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
                        getFirstOrder();
                } else {
                    getFirstOrder();
                }

            }

            @Override
            public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("eeeeeeeeeeeeeeee", "onFailure:update order " + t.getMessage());
                //  finishTheWorkNow();
            }
        });
    }

    void showProblemNoteDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("مشكلة");
        //  alertDialog.setMessage("اكتب المشكلة");

        final EditText input = new EditText(this);
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
                            api.sendProblem(SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN),
                                    Integer.parseInt(SharedHelper.getKey(getApplicationContext(), LoginActivity.USER_ID)),
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
                            Toast.makeText(OrderActivity.this, "برجاء ادخال المشكلة", Toast.LENGTH_SHORT).show();
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

    Timer updateOrderTimer;
    int updateOrderTimerCount = 0;

    private void updateOrderTimer() {
        updateOrderTimer = new Timer();
        updateOrderTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (updateOrderTimerCount == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateOrderTimerCount = updateOrderTimerCount + 1;
                            warningDialog.show();
                        }
                    });
                } else
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            api.userWorkTime(SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN),
                                    SharedHelper.getKey(getApplicationContext(), LoginActivity.USER_ID), "-300")
                                    .enqueue(new Callback<UserWorkTimeResponse>() {
                                        @Override
                                        public void onResponse(Call<UserWorkTimeResponse> call, Response<UserWorkTimeResponse> response) {
                                            if (response.body() != null) {
                                                Toast.makeText(OrderActivity.this, "تم خصم 5 دقائق", Toast.LENGTH_SHORT).show();

                                                Log.d("dddddddddd", "onResponse: " + response.body().getData());
                                                Log.d("dddddddddd", "time after end: " + timeWork);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserWorkTimeResponse> call, Throwable t) {
                                            Log.d("dddddddddd", "onResponse: " + t.getMessage());
                                        }
                                    });
                        }
                    });

            }
        }, 30000, 30000);
    }

    Dialog warningDialog;

    private void createWarningDialog() {
        warningDialog = new Dialog(this);
        warningDialog.setContentView(R.layout.dialog_universal_warning);
        RobotoTextView ok = warningDialog.findViewById(R.id.ok_warning);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
            }
        });
    }

    public void startTimer(long start) {
        timerTextView.setStarterTime(start);
        timerTextView.startTimer();
    }

    public void pauseServiceTimer() {
        cancelWorkTimer();
        stoped = true;
    }

    Order order;
    String orderStatus = null;

    private void getFirstOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("جار جلب الطلب");
        progressDialog.show();


        api.getOrders(SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID)).enqueue(new Callback<UserOrders>() {
            @Override
            public void onResponse(Call<UserOrders> call, Response<UserOrders> response) {
                if (response.body() != null) {
                    progressDialog.dismiss();
                    if (response.body().getCode().equals("1202") || response.body().getCode().equals("1200")) {
                        orderStatus = response.body().getOrder_type();
//
//                        if (5 == 5) {
//                            missed_call_view.setVisibility(View.VISIBLE);
//                            orederView.setVisibility(View.GONE);
//                            return;
//                        }
                        if (response.body().getOrder_type().equals("missed_call") || response.body().getOrder_type().equals("order_exsist")) {
                            missed_call_view.setVisibility(View.VISIBLE);
                            orederView.setVisibility(View.GONE);
                            return;
                        } else {
                            missed_call_view.setVisibility(View.GONE);
                            orederView.setVisibility(View.VISIBLE);
                        }

                        order = response.body().getOrder();
                        if (order != null) {
                            order_ud = order.getId();
                            discount.setText(order.getDiscount());
                            product.setText(order.getProduct_name());
                            status.setText(order.getOrder_status());
                            client_name.setText(order.getClient_name());
                            client_address.setText(order.getClient_address());
                            client_area.setText(order.getClient_area());
                            client_city.setText(order.getClient_city());
                            phone = order.getPhone_1();
                            shipping_status.setText(order.getOrder_shipping_status());
                            client_order_phone1.setText(order.getPhone_1());

                            item_cost.setText(order.getItem_cost());
                            item_no.setText(order.getItems_no());

                            order_cost.setText(order.getOrder_cost());
                            order_total_cost.setText(order.getTotal_order_cost());
                            sender_name.setText(order.getSender_name());
                            //order_type.setText(order.getOrder_type());
                            client_feedback.setText(order.getClient_feedback());

                            if (!stoped) ;
                            callClient();

                        } else {
                        }

                    } else {
                        SharedHelper.putKey(getApplicationContext(), IS_LOGIN, "اعادة تسجيل الدخول");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finishTheWorkNow();

                    }
                    Log.d("eeeeeeeee", "onResponse: " + response.body().getInfo());
                }
            }

            @Override
            public void onFailure(Call<UserOrders> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("eeeeeeeee", "onResponse: " + t.getMessage());
            }
        });
    }

    long currentCallTimerCount;
    Timer currentCallTimer;

    public void callClient() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        Log.d("xxxxxxxxxx", "callClient: " + phone);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1232);
            }
        } else {
            startActivity(callIntent);
        }
        currentCallTimer = new Timer();
        currentCallTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentCallTimerCount += 1;
            }
        }, 0, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1232) {
            callClient();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!finish) {
            pauseActivityTimer = new Timer();
            pauseActivityTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    pauseActivityTimerCount += 1;
                    if (pauseActivityTimerCount == 2) {
                        pauseActivityTimerCount = 0;
                        Intent intent = new Intent(getApplicationContext(), EmptyCallActivity.class);
                        startActivity(intent);
                    }
                }
            }, 0, 1000);
        } else {
            stoped = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        finish = false;
        if (pauseActivityTimer != null) {
            pauseActivityTimer.cancel();
            pauseActivityTimer = null;
        }

        if (orderStatus != null) {
            if (orderStatus.equals("new_order") || orderStatus.equals("pending_order")) {
                initUpdateAsNewOrder();
            } else {
                initUpdateAsOldOrder();
            }
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
//        micMode = menu.findItem(R.id.mic_mode);
        if (askToFinishWork) {
            finishWok.setVisible(true);
            askToFinishWortkItem.setVisible(false);
        } else {
            finishWok.setVisible(false);
            askToFinishWortkItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.finish_work) {
            finishTheWorkNow();
        } else if (id == R.id.end_call) {
            endCAll();
        } else if (id == R.id.call_client) {
            callClient();
        } else if (id == R.id.ask_finish_work) {
            setAskToFinishWork();
            invalidateOptionsMenu();
        } else if (id == R.id.mic_mode) {
            modifyMic();

        }else if (id == R.id.calc) {
            showCalculatoe();
        }
        return super.onOptionsItemSelected(item);
    }


    public static void finishTheWorkNow() {
        finish = true;
        orderActivity.finish();
    }

    void modifyMic() {

        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
            micMute = false;
            checkMic();
        } else {
            audioManager.setMicrophoneMute(true);
            micMute = true;
            checkMic();
        }
    }

    void checkMic() {
        if (!micMute) {
            micMode.setIcon(R.drawable.ic_mic_none_black_24dp);
            micMode.setTitle(getString(R.string.mute_mic));

        } else {
            micMode.setIcon(R.drawable.ic_mic_off_black_24dp);
            micMode.setTitle(getString(R.string.turn_on_mic));
        }
    }

    void endCAll() {
        PhoneCallReceiver.enCallFromMe = true;
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

    void setAskToFinishWork() {

        final Dialog builder = new Dialog(this);

        builder.setContentView(R.layout.dialog_ask_finigh);
        RobotoTextView ok = builder.findViewById(R.id.yes);
        RobotoTextView ca = builder.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askToFinishWork = true;
                invalidateOptionsMenu();
                builder.dismiss();
            }
        });
        ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

//        builder.setPositiveButton(R.string.finish_work, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                askToFinishWork = true;
//                invalidateOptionsMenu();
//            }
//        });
//
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });

        builder.show();
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
        if (pauseActivityTimer != null) {
            pauseActivityTimer.cancel();
        }
        if (updateOrderTimer != null) {
            updateOrderTimer.cancel();
        }
        Log.d("dddddddddd", "time before end: " + timeWork);
        api.userWorkTime(SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID), String.valueOf(timeWork))
                .enqueue(new Callback<UserWorkTimeResponse>() {
                    @Override
                    public void onResponse(Call<UserWorkTimeResponse> call, Response<UserWorkTimeResponse> response) {
                        if (response.body() != null) {

                            Log.d("dddddddddd", "onResponse: " + response.body().getData());
                            Log.d("dddddddddd", "time after end: " + timeWork);
                            pauseServiceTimer();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserWorkTimeResponse> call, Throwable t) {
                        pauseServiceTimer();
                        Log.d("dddddddddd", "onResponse: " + t.getMessage());
                    }
                });
    }

    String phoneNumber = null;
    String callDuration2 = null;
    String calType = null;
    String activatedNum;

    LastCallDetails getLastCallDetails() {
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        Cursor managedCursor = getContentResolver().query(contacts, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int activeID = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);

        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            phoneNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            callDuration2 = managedCursor.getString(duration);
            activatedNum = managedCursor.getString(activeID);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    dir = "REJECTED";
                    break;
            }

            calType = dir;

        }

        managedCursor.close();
        return new LastCallDetails(phoneNumber, callDuration2, activatedNum, calType);

    }

    @Override
    public void callEnded() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LastCallDetails callDetails = getLastCallDetails();

                        if (callDetails.getType().equals("MISSED") || callDetails.getType().equals("REJECTED")) {
                            if (callDetails.getActiveId().equals(SharedHelper.getKey(getApplicationContext(),
                                    "activated_sub_id"))) {
                                Toast.makeText(OrderActivity.this, "تم ارسال رقم  " + callDetails.getPhone() + " المكالمة الفائتة الى السيرفر", Toast.LENGTH_SHORT).show();

                            }

                        } else if (callDetails.getType().equals("OUTGOING")) {

                            if (callDetails.getDuration().equals("0")) {
                                if (currentCallTimerCount <= 30) {
                                    currentCallTimerCount = 0;
                                    if (currentCallTimer != null)
                                        currentCallTimer.cancel();
                                    Toast.makeText(OrderActivity.this, "تم انهاء الطلب العميل مشغول او غير متاح", Toast.LENGTH_SHORT).show();
                                    updateOrder(client_busy);
                                } else {
                                    currentCallTimerCount = 0;
                                    if (currentCallTimer != null)
                                        currentCallTimer.cancel();
                                    Toast.makeText(OrderActivity.this, "تم انهاء الطلب العميل لم يرد", Toast.LENGTH_SHORT).show();
                                    updateOrder(client_noanswer);
                                }
                            } else {
                                updateOrderTimer();
                            }
                        }


                    }
                });
            }
        }, 1000);

    }

    public static boolean stoped = false;
    Timer workTimer;

    void startWorkTimerTimer() {
        workTimer = new Timer();
        workTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!stoped) {
                    timeWork += 1;
                }

            }
        }, 0, 1000);
    }

    void cancelWorkTimer() {
        if (workTimer != null) {
            workTimer.cancel();
            stoped = true;
            timeWork = 0;
        }
    }

    @Override
    public void onFragmentInteraction() {
        Toast.makeText(this, "تم ارسال الطلب", Toast.LENGTH_SHORT).show();
        getFirstOrder();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.finish_work) {
                finishTheWorkNow();
            } else if (id == R.id.end_call) {
                endCAll();
            } else if (id == R.id.call_client) {
                callClient();
            } else if (id == R.id.ask_finish_work) {
                setAskToFinishWork();
                invalidateOptionsMenu();
            } else if (id == R.id.mic_mode) {
                modifyMic();

            }
            return false;
        }
    };

    @Override
    public void orderSentGetNewOrder() {
        Toast.makeText(this, "تم ارسال الطلب", Toast.LENGTH_SHORT).show();
        getFirstOrder();
    }

    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                onConnectionFound();
            } else {
                onConnectionLost();
            }
        }
    };

    public void onConnectionLost() {
        Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 8523);
    }

    public void onConnectionFound() {
    }

    private void registerBroadCastReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    public void showLabel(View view) {
        LinearLayout linearLayout = findViewById(view.getId());

        RunAnimation(linearLayout.getChildAt(0).getId(),linearLayout.getChildAt(1).getId());
    }

    private void RunAnimation(int id1,int id2) {

        TextView tv = (TextView) findViewById(id1);
        TextView bg = (TextView) findViewById(id2);

        if (tv.getVisibility() == View.VISIBLE) {
            Animation a = AnimationUtils.loadAnimation(this, R.anim.bottom_sheet_fad_out);
            a.reset();
            tv.clearAnimation();
            tv.startAnimation(a);
            tv.setVisibility(View.GONE);
            bg.setBackgroundResource(R.drawable.ic_background_gray);
        } else {
            Animation a = AnimationUtils.loadAnimation(this, R.anim.bottom_sheet_fad_in);
            a.reset();
            tv.clearAnimation();
            tv.startAnimation(a);
            tv.setVisibility(View.VISIBLE);
            bg.setBackgroundResource(R.drawable.ic_background_gray_down);

        }

    }
    private void showCalculatoe() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("calc_dialog") == null) {
            calcDialog.show(fm, "calc_dialog");
        }
    }
    CalcDialog calcDialog;
    private void setCalc(Bundle state) {
        if (state != null) {
            String valueStr = state.getString("value");
            if (valueStr != null) {
                value = new BigDecimal(valueStr);
            }
        }

         calcDialog = CalcDialog.newInstance(DIALOG_REQUEST_CODE);

        signChk = findViewById(R.id.chk_change_sign);
        if (value == null) signChk.setEnabled(false);

        final CheckBox showAnswerChk = findViewById(R.id.chk_answer_btn);
        final CheckBox showSignChk = findViewById(R.id.chk_show_sign);
        final CheckBox clearOnOpChk = findViewById(R.id.chk_clear_operation);
        final CheckBox showZeroChk = findViewById(R.id.chk_show_zero);

        // Max value
        final CheckBox maxValChk = findViewById(R.id.chk_max_value);
        final EditText maxValEdt = findViewById(R.id.edt_max_value);
        maxValChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maxValEdt.setEnabled(isChecked);
            }
        });
        maxValEdt.setEnabled(maxValChk.isChecked());
        maxValEdt.setText(String.valueOf(10000000000L));

        // Max integer digits
        final CheckBox maxIntChk = findViewById(R.id.chk_max_int);
        final EditText maxIntEdt = findViewById(R.id.edt_max_int);
        maxIntChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maxIntEdt.setEnabled(isChecked);
            }
        });
        maxIntEdt.setEnabled(maxIntChk.isChecked());
        maxIntEdt.setText(String.valueOf(10));

        // Max fractional digits
        final CheckBox maxFracChk = findViewById(R.id.chk_max_frac);
        final EditText maxFracEdt = findViewById(R.id.edt_max_frac);
        maxIntChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maxFracEdt.setEnabled(isChecked);
            }
        });
        maxFracEdt.setEnabled(maxFracChk.isChecked());
        maxFracEdt.setText(String.valueOf(8));

        // Value display
        valueTxv = findViewById(R.id.txv_result);
        valueTxv.setText(value == null ? getString(R.string.result_value_none) : value.toPlainString());

        // Open dialog button
        showCalc(maxValEdt, maxValChk, maxIntEdt, maxIntChk, maxFracEdt, maxFracChk, calcDialog, showSignChk, showAnswerChk, clearOnOpChk, showZeroChk);

    }

    private void showCalc(EditText maxValEdt, CheckBox maxValChk, EditText maxIntEdt, CheckBox maxIntChk, EditText maxFracEdt, CheckBox maxFracChk, CalcDialog calcDialog, CheckBox showSignChk, CheckBox showAnswerChk, CheckBox clearOnOpChk, CheckBox showZeroChk) {
        boolean signCanBeChanged = !signChk.isEnabled() || signChk.isChecked();

        String maxValueStr = maxValEdt.getText().toString();
        BigDecimal maxValue = maxValChk.isChecked() && !maxValueStr.isEmpty() ?
                new BigDecimal(maxValueStr) : null;

        String maxIntStr = maxIntEdt.getText().toString();
        int maxInt = maxIntChk.isChecked() && !maxIntStr.isEmpty() ?
                Integer.valueOf(maxIntStr) : CalcDialog.MAX_DIGITS_UNLIMITED;

        String maxFracStr = maxFracEdt.getText().toString();
        int maxFrac = maxFracChk.isChecked() && !maxFracStr.isEmpty() ?
                Integer.valueOf(maxFracStr) : CalcDialog.MAX_DIGITS_UNLIMITED;

        // Set settings and value
        calcDialog.setValue(value)
                .setShowSignButton(showSignChk.isChecked())
                .setShowAnswerButton(showAnswerChk.isChecked())
                .setSignCanBeChanged(signCanBeChanged, signCanBeChanged ? 0 : value.signum())
                .setClearDisplayOnOperation(clearOnOpChk.isChecked())
                .setShowZeroWhenNoValue(showZeroChk.isChecked())
                .setMaxValue(maxValue)
                .setMaxDigits(maxInt, maxFrac);


    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        if (value != null) {
            state.putString("value", value.toString());
        }
    }

    @Override
    public void onValueEntered(int requestCode, BigDecimal value) {
        // if (requestCode == DIALOG_REQUEST_CODE) {}  <-- If there's many dialogs

        this.value = value;

        valueTxv.setText(value.toPlainString());
        signChk.setEnabled(value.compareTo(BigDecimal.ZERO) != 0);
    }

}

