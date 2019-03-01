package com.greyeg.tajr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.greyeg.tajr.R;
import com.greyeg.tajr.adapters.ProductSpinnerAdapter;
import com.greyeg.tajr.adapters.ProductsAdapter;
import com.greyeg.tajr.calc.CalcDialog;
import com.greyeg.tajr.call_receiver.PhoneCallReceiver;
import com.greyeg.tajr.fragments.NewOrderFragment;
import com.greyeg.tajr.fragments.SearchOrderPhoneFragment;
import com.greyeg.tajr.helper.CurrentCallListener;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.TimerTextView;
import com.greyeg.tajr.helper.font.RobotoTextView;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.DeleteAddProductResponse;
import com.greyeg.tajr.models.LastCallDetails;
import com.greyeg.tajr.models.ProductData;
import com.greyeg.tajr.models.ProductForSpinner;
import com.greyeg.tajr.models.SimpleOrderResponse;
import com.greyeg.tajr.models.UpdateOrderResponse;
import com.greyeg.tajr.models.UpdateOrdreDataRespnse;
import com.greyeg.tajr.models.UpdateOrederNewResponse;
import com.greyeg.tajr.models.UploadPhoneResponse;
import com.greyeg.tajr.models.UploadVoiceResponse;
import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.over.FloatLayout;
import com.greyeg.tajr.records.CallDetails;
import com.greyeg.tajr.records.CallsReceiver;
import com.greyeg.tajr.records.DatabaseManager;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;
import com.greyeg.tajr.sheets.TopSheetBehavior;
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.greyeg.tajr.activities.LoginActivity.IS_LOGIN;

public class OrderActivity extends AppCompatActivity
        implements CurrentCallListener,
        SearchOrderPhoneFragment.OnFragmentInteractionListener,
        NewOrderFragment.SendOrderListener,
        CalcDialog.CalcDialogCallback {
    public static final String client_busy = "client_busy";
    public static final String client_cancel = "client_cancel";
    public static final String client_noanswer = "client_noanswer";
    public static final String order_data_confirmed = "order_data_confirmed";
    public static final String client_phone_error = "client_phone_error";
    public static final String client_delay = "client_delay ";


    private static final String TAG = CalcActivity.class.getSimpleName();
    private static final int DIALOG_REQUEST_CODE = 0;
    public static TimerTextView timerTextView;
    public static String phone;
    public static Activity my;
    public static long timeWork;
    public static boolean finish = false;
    public static boolean askToFinishWork = false;
    public static Activity orderActivity;
    public static boolean stoped = false;
    public AllProducts allProducts;


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

    LinearLayoutManager productsLinearLayoutManager;

    //    @BindView(R.id.update)
    //    DrawMeButton update;
    //
    //    @BindView(R.id.cancel)
    //    DrawMeButton cancel;

    @BindView(R.id.discount)
    EditText discount;
    @BindView(R.id.order_cost)
    EditText order_cost;
    @BindView(R.id.order_total_cost)
    EditText order_total_cost;
    @BindView(R.id.client_feedback)
    EditText client_feedback;
    @BindView(R.id.order_id)
    EditText order_id;
    DatabaseManager databaseManager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.products_view)
    View productsView;

    @BindView(R.id.client_phone_error2)
    FloatingActionButton client_phone_error2;

    @BindView(R.id.order_shipper_confirmed2)
    FloatingActionButton order_shipper_confirmed2;

    @BindView(R.id.order_data_confirmed2)
    FloatingActionButton order_data_confirmed2;

    @BindView(R.id.problem2)
    FloatingActionButton problem2;

    @BindView(R.id.client_city)
    Spinner client_city;


    long startWorkTime;

    RadioGroup radioGroup;
    Button ok;
    View view;
    String newStatus;
    String newStatusTag;

    Timer pauseActivityTimer;

    long pauseActivityTimerCount;
    ProgressDialog progressDialog;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    boolean micMute = false;
    @BindView(R.id.order_view)
    View orederView;
    @BindView(R.id.missed_call_view)
    View missed_call_view;
    @BindView(R.id.ProgressBar)
    ProgressBar mProgressBar4;
    @BindView(R.id.present)
    TextView present;

    String connectionType;
    boolean hasInternet;
    Menu naveMenu;

    Timer updateOrderTimer;
    int updateOrderTimerCount = 0;
    Dialog warningDialog;
    public static SimpleOrderResponse.Order order;
    String orderStatus = null;
    boolean firstOrder;
    int firstRemaining;
    int finishedOrders;
    long currentCallTimerCount;
    Timer currentCallTimer;
    MenuItem askToFinishWortkItem;
    MenuItem finishWok;
    MenuItem micMode;
    boolean productShowen;
    ProductData currentProduct;
    //    @BindView(R.id.product_view)
//    View productView;
    String phoneNumber = null;
    String callDuration2 = null;
    String calType = null;
    String activatedNum;
    List<CallDetails> callDetailsList;
    Timer workTimer;

    CalcDialog calcDialog;
    private TextView valueTxv;
    private CheckBox signChk;
    private @Nullable
    BigDecimal value;
    private String order_ud = null;
    private Api api;
    private Disposable networkDisposable;
    private Disposable internetDisposable;
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
                callClient(phone);
            } else if (id == R.id.ask_finish_work) {
                setAskToFinishWork();
                invalidateOptionsMenu();
            } else if (id == R.id.mic_mode) {
                modifyMic();

            }
            return false;
        }
    };

    public static void finishTheWorkNow() {
        finish = true;
        orderActivity.finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order);
        ButterKnife.bind(this);
        openRecords();
        getProducts();
        setConnectionListener();
        setUpProgressBar();
        setCalc(savedInstanceState);
        databaseManager = new DatabaseManager(this);
        orderActivity = this;
        CallsReceiver.setCurrentCallListener(this);
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
        productsLinearLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(productsLinearLayoutManager);

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

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        BottomNavigationView navigation = findViewById(R.id.navigation);
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

        Api api = BaseClient.getBaseClient().create(Api.class);
        getcities(api);

        createWarningDialog();
        Log.d("dddddddddd", "time started: " + timeWork);


        View sheet = findViewById(R.id.top_sheet);
        TopSheetBehavior tt = TopSheetBehavior.from(sheet);

        tt.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                animationShow();
                Log.d("TAG", "newState: " + newState);

                if (newState == 3) {
                    findViewById(R.id.to_bottom_button).setVisibility(View.GONE);
                    findViewById(R.id.shadow).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.to_bottom_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.shadow).setVisibility(View.GONE);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset, Boolean isOpening) {
                Log.d("TAG", "slideOffset: " + slideOffset);
                if (isOpening != null) {
                    Log.d("TAG", "isOpening: " + isOpening);

                    if (isOpening) {
                        findViewById(R.id.to_bottom_button).setVisibility(View.GONE);
                        findViewById(R.id.shadow).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.to_bottom_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.shadow).setVisibility(View.GONE);

                    }
                }
            }
        });

    }

    private void setConnectionListener() {
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    Log.d("ssssssssssssss", connectivity.toString());
                    final NetworkInfo.State state = connectivity.state();
                    connectionType = connectivity.typeName();   // WIFI OR MOBILE

                    //  tvConnectivityStatus.setText(String.format("state: %s, typeName: %s", state, name));
                    Log.d("ssssssssssssss", "onCreate: " + String.format("state: %s, typeName: %s", state, connectionType));
                });

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    hasInternet = isConnected;
                    //    tvInternetStatus.setText(isConnected.toString());
                    if (!isConnected) {
                        onConnectionLost();
                    }
                    Log.d("ssssssssssssss", "onCreate: " + isConnected.toString());
                });
    }

    @BindView(R.id.right_menu)
    FloatingActionMenu floatingActionMenu;

    @OnClick(R.id.cancel_order2)
    void cancelOrder() {
        floatingActionMenu.close(true);
        updateOrder(client_cancel);
    }

    @OnClick(R.id.problem2)
    void problem() {
        floatingActionMenu.close(true);
        showProblemNoteDialog();
    }

    @OnClick(R.id.client_phone_error2)
    void client_phone_error() {
        floatingActionMenu.close(true);
        updateOrder(client_phone_error);
    }

    @OnClick(R.id.delay)
    void clent_delay() {
        floatingActionMenu.close(true);
        updateOrder(client_delay);
    }

    @OnClick(R.id.no_answer)
    void no_answer() {
        floatingActionMenu.close(true);
        updateOrder(client_noanswer);
    }

    @OnClick(R.id.busy)
    void busy() {
        floatingActionMenu.close(true);
        updateOrder(client_busy);
    }

    @OnClick(R.id.order_data_confirmed2)
    void order_data_confirmed() {
        floatingActionMenu.close(true);
        updateOrder(order_data_confirmed);
    }

    void updateOrderData() {

        Api api = BaseClient.getBaseClient().create(Api.class);
        api.updateOrderData(
                SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID),
                CITY_ID,
                client_name.getText().toString(),
                client_address.getText().toString(),
                client_area.getText().toString(),
                item_no.getText().toString(),
                notes.getText().toString(),
                discount.getText().toString()

        ).enqueue(new Callback<UpdateOrdreDataRespnse>() {
            @Override
            public void onResponse(Call<UpdateOrdreDataRespnse> call, Response<UpdateOrdreDataRespnse> response) {
                Log.d("555555", "onResponse: orders updates");


            }

            @Override
            public void onFailure(Call<UpdateOrdreDataRespnse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    void updateOrder(String value) {
        updateOrderData();
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
                order_ud,
                value
        ).enqueue(new Callback<UpdateOrederNewResponse>() {
            @Override
            public void onResponse(@NotNull Call<UpdateOrederNewResponse> call, @NotNull Response<UpdateOrederNewResponse> response) {
                Log.d("eeeeeeeeeeeeee", "onResponse: " + value + response.body().getCode());

                if (response.body().getCode().equals("1200") || response.body().getCode().equals("1202")) {
                    Log.d("eeeeeeeeeeeeee", "onResponse: " + value + response.body().getCode());
                    progressBar.setVisibility(View.GONE);
                    if (askToFinishWork) {
                        finishTheWorkNow();
                    } else
                        getFirstOrder();
                } else {
                    getFirstOrder();
                }

            }

            @Override
            public void onFailure(Call<UpdateOrederNewResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("dddddddddd", "onFailure:update " + value + t.getMessage());
                Toast.makeText(OrderActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void confirm_shipper_status(String value) {
        updateOrderData();
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
                order_ud,
                value
        ).enqueue(new Callback<UpdateOrederNewResponse>() {
            @Override
            public void onResponse(@NotNull Call<UpdateOrederNewResponse> call, @NotNull Response<UpdateOrederNewResponse> response) {
                Log.d("eeeeeeeeeeeeee", "onResponse: " + value + response.body().getCode());

                if (response.body().getCode().equals("1200") || response.body().getCode().equals("1202")) {
                    Log.d("eeeeeeeeeeeeee", "onResponse: " + value + response.body().getCode());
                    progressBar.setVisibility(View.GONE);
                    if (askToFinishWork) {
                        finishTheWorkNow();
                    } else
                        getFirstOrder();
                } else {
                    getFirstOrder();
                }

            }

            @Override
            public void onFailure(Call<UpdateOrederNewResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("dddddddddd", "onFailure:update " + value + t.getMessage());
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
                        progressBar.setVisibility(View.VISIBLE);
                        if (!input.getText().toString().equals("")) {
                            api.sendProblem(SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN),
                                    Integer.parseInt(SharedHelper.getKey(getApplicationContext(), LoginActivity.USER_ID)),
                                    Integer.parseInt(order.getId()),
                                    input.getText().toString()).enqueue(new Callback<UpdateOrderResponse>() {
                                @Override
                                public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                                    if (response.body() != null) {
                                        if (response.body().getCode().equals("1200")) {

                                            progressBar.setVisibility(View.GONE);
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

    ProductsAdapter productsAdapter;

    private void getFirstOrder() {
        progressBar.setVisibility(View.VISIBLE);

        api.getFuckenOrders(SharedHelper.getKey(this, LoginActivity.TOKEN)).enqueue(new Callback<SimpleOrderResponse>() {
            @Override
            public void onResponse(Call<SimpleOrderResponse> call, Response<SimpleOrderResponse> response) {
                if (response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    if (response.body().getCode().equals("1202") || response.body().getCode().equals("1200")) {
                        orderStatus = response.body().getOrder_type();
                        if (!firstOrder) {

                            firstOrder = true;
                            firstRemaining = response.body().getRemainig_orders();
                            mProgressBar4.setMax(firstRemaining);

                        }

                        int b = firstRemaining - response.body().getRemainig_orders();
                        mProgressBar4.setProgress(b);
                        createNotification(String.valueOf(firstRemaining - b));
                        int a = (int) (100 * Float.parseFloat(String.valueOf(finishedOrders)) / Float.parseFloat(String.valueOf(firstRemaining)));
                        String test = String.valueOf(response.body().getRemainig_orders()) + "  (" + String.valueOf(a) + "%)";
                        present.setText(test);
                        finishedOrders++;

                        if (response.body().getOrder_type().equals("missed_call") || response.body().getOrder_type().equals("order_exsist")) {
                            showMissedCall();
                            return;
                        } else {
                            showOrderView();
                        }

                        if (response.body().getOrder_type().equals("new_order") || response.body().getOrder_type().equals("pending_order")) {
                            problem2.setVisibility(View.GONE);
                            order_shipper_confirmed2.setVisibility(View.GONE);
                            order_data_confirmed2.setVisibility(View.VISIBLE);
                            client_phone_error2.setVisibility(View.VISIBLE);
                            shipping_status.setVisibility(View.GONE);
                        } else {
                            shipping_status.setVisibility(View.VISIBLE);
                            problem2.setVisibility(View.VISIBLE);
                            order_shipper_confirmed2.setVisibility(View.VISIBLE);
                            order_data_confirmed2.setVisibility(View.GONE);
                            client_phone_error2.setVisibility(View.GONE);
                        }

                        order = response.body().getOrder();
                        if (order != null) {
                            order_ud = order.getId();
                            discount.setText(order.getDiscount());
                            setSingleProducts(order.getProduct_id());
                            status.setText(order.getOrder_status());
                            client_name.setText(order.getClient_name());
                            client_address.setText(order.getClient_address());
                            client_area.setText(order.getClient_area());
                            int cityIndex = citiesId.indexOf(order.getClient_city_id());
                            client_city.setSelection(cityIndex);
                            SHIPPING_COST = shippingCosts.get(cityIndex);
                            client_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    CITY_ID = String.valueOf(citiesId.get(position));
                                    SHIPPING_COST = shippingCosts.get(position);

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            shipping_cost.setText(SHIPPING_COST);
                            phone = order.getPhone_1();
                            shipping_status.setText(order.getOrder_shipping_status());
                            client_order_phone1.setText(order.getPhone_1());
                            order_id.setText(order.getId());
                            item_cost.setText(order.getItem_cost());
                            item_no.setText(order.getItems_no());

                            order_cost.setText(order.getOrder_cost());
                            order_total_cost.setText(order.getTotal_order_cost());
                            sender_name.setText(order.getSender_name());
                            //order_type.setText(order.getOrder_type());
                            client_feedback.setText(order.getClient_feedback());
                            notes.setText(order.getNotes());
                            //   if (!stoped) ;
                            //   callClient(order.getPhone_1());

                            productsAdapter = new ProductsAdapter(getApplicationContext(), order.getMulti_orders(), order.getId());
                            if (order.getMulti_orders().size() > 0) {
                                Log.d("ProductsAdapter", "onResponse: " + order.getMulti_orders().get(0).getProduct_name());
                                productsView.setVisibility(View.VISIBLE);
                                productsRecyclerView.setAdapter(productsAdapter);
//                                product_label_view.setVisibility(View.GONE);

                            } else {
//                                product_label_view.setVisibility(View.VISIBLE);
                                productsView.setVisibility(View.GONE);
                            }

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
            public void onFailure(Call<SimpleOrderResponse> call, Throwable t) {
                Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                //   finish();
            }
        });
    }


    List<String> cities = new ArrayList<>();
    List<String> citiesId = new ArrayList<>();
    List<String> shippingCosts = new ArrayList<>();

    public static String CITY_ID;
    String SHIPPING_COST;

    private List<Cities.City> citiesBody;

    private void getcities(Api api) {
        progressBar.setVisibility(View.VISIBLE);
        if (cities != null && cities.size() > 1) {
            cities.clear();
        }
        Call<Cities> getCiriesCall = api.getCities(
                SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID)
        );

        getCiriesCall.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null && response.body().getCities() != null) {
                    if (response.body().getCities().size() > 0) {

                        citiesBody = response.body().getCities();
                        for (Cities.City city : citiesBody) {
                            cities.add(city.getCity_name());
                            citiesId.add(city.getCity_id());
                            shippingCosts.add(city.getShipping_cost());
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.layout_cities_spinner_item, cities);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        client_city.setAdapter(adapter);
                        getFirstOrder();
                    }
                } else {
                    SharedHelper.putKey(getApplicationContext(), IS_LOGIN, "اعادة تسجيل الدخول");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finishTheWorkNow();

                }

            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showOrderView() {
        missed_call_view.setVisibility(View.GONE);
        orederView.setVisibility(View.VISIBLE);
        floatingActionMenu.showMenu(true);
        top_sheet.setVisibility(View.VISIBLE);
    }

    private void showMissedCall() {
        missed_call_view.setVisibility(View.VISIBLE);
        orederView.setVisibility(View.GONE);
        top_sheet.setVisibility(View.GONE);
        floatingActionMenu.hideMenu(true);
    }

    @BindView(R.id.product_label_view)
    View product_label_view;


    @BindView(R.id.top_sheet)
    View top_sheet;

    public void callClient(String phone) {

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
            //callClient(phone);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CallsReceiver.inOrderActivity = false;
        // startService(new Intent(this, FloatLayout.class));


//        if (!finish) {
//            pauseActivityTimer = new Timer();
//            pauseActivityTimer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    pauseActivityTimerCount += 1;
//                    if (pauseActivityTimerCount == 2) {
//                        pauseActivityTimerCount = 0;
//                        Intent intent = new Intent(getApplicationContext(), EmptyCallActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            }, 0, 1000);
//        } else {
//            stoped = true;
//        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        CallsReceiver.inOrderActivity = true;
        //  stopService(new Intent(this, FloatLayout.class));
        finish = false;
//        if (pauseActivityTimer != null) {
//            pauseActivityTimer.cancel();
//            pauseActivityTimer = null;
//        }

//        if (orderStatus != null) {
//            if (orderStatus.equals("new_order") || orderStatus.equals("pending_order")) {
//                initUpdateAsNewOrder();
//            } else {
//                initUpdateAsOldOrder();
//            }
//        }
    }

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
            finishWok.setVisible(true);
            askToFinishWortkItem.setVisible(false);
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
            callClient(phone);
        } else if (id == R.id.ask_finish_work) {
            setAskToFinishWork();
            invalidateOptionsMenu();
        } else if (id == R.id.mic_mode) {
            modifyMic();

        } else if (id == R.id.calc) {
            showCalculatoe();
        } else if (id == R.id.show_missed_call) {
            if (!inMissed) {
                inMissed = true;
                showMissedCall();
            } else {
                inMissed = false;
                showOrderView();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    boolean inMissed = false;

    void showProductDetails() {

        if (!productShowen) {
            productShowen = true;
            animationShow();
        } else {
            productShowen = false;

        }
    }

    private void animationShow() {

        if (order == null)
            return;
        if (allProducts != null) {
            for (ProductData data : allProducts.getProducts()) {
                if (data.getProduct_name().equals(order.getProduct_name())) {
                    currentProduct = data;
                }
            }

        }

        TextView textView = findViewById(R.id.name);
        TextView price = findViewById(R.id.price);
        TextView description = findViewById(R.id.description);
        TextView category_name = findViewById(R.id.category_name);
        TextView product_info = findViewById(R.id.product_info);

        SimpleDraweeView image = findViewById(R.id.product_image);
        image.setImageURI(currentProduct.getProduct_image());
        textView.setText(currentProduct.getProduct_name());
        description.setText(currentProduct.getProduct_describtion());
        price.setText(currentProduct.getProduct_real_price() + " " + getString(R.string.omla));
        category_name.setText(currentProduct.getCategory_name());
        product_info.setText(currentProduct.getProduct_info());

    }

//    private void animationHide() {
//        productView.setVisibility(View.GONE);
//    }

    @SuppressLint("NewApi")
    public void createNotification(String first) {

        Intent intent2 = new Intent(getApplicationContext(), OrderActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, 0);

//        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_launcher);
//
//        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.notification_icon, getResources().getString(R.string.cancel), pendingIntent2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("5", "eslam", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("5");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "5")
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle("orders")
                .setOngoing(true)
                .setColor(Color.RED)
                // .addAction(action)
                .setContentText(getString(R.string.remaining) + " " + first + " " + getString(R.string.order))
                .setSmallIcon(R.drawable.ic_launcher);

        Intent intent = new Intent(this, OrderActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(this);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //builder.setContentIntent(pendingIntent);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(5, builder.build());
        Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();


    }

//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("5", "eslam", importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }


    public void updateNotificationText(String inString) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentText(inString)
                .setOngoing(true)
                .setContentTitle("orders")
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true)
                .setAutoCancel(false);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(intent);
        stackBuilder.addParentStack(ChatActivity.class);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        notificationManager.notify(5, builder.build());
    }

    public void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(5);
    }

    protected synchronized void getProducts() {
        Api api = BaseClient.getBaseClient().create(Api.class);
        api.getProducts(SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID)
        ).enqueue(new Callback<AllProducts>() {
            @Override
            public void onResponse(Call<AllProducts> call, final Response<AllProducts> response) {
                Log.d("eeeeeeeeeeeeeee", "respons: " + response.body().getProducts_count());
                if (response.body() != null) {
                    allProducts = response.body();

                }
            }

            @Override
            public void onFailure(Call<AllProducts> call, Throwable t) {
                Log.d("eeeeeeeeeeeeeee", "onFailure: " + t.getMessage());
            }
        });

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
        //stopService(new Intent(this, FloatLayout.class));

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
        cancelNotification();
        closeRecords();
        stopService(new Intent(this, FloatLayout.class));
        //  unregisterReceiver(networkStateReceiver);
        if (pauseActivityTimer != null) {
            pauseActivityTimer.cancel();
        }

        if (updateOrderTimer != null) {
            updateOrderTimer.cancel();
        }
        Log.d("dddddddddd", "time before end: " + timeWork);
        long currentWorkTime = getNotSavedWrokTime() + timeWork;
        api.userWorkTime(SharedHelper.getKey(this, LoginActivity.TOKEN),
                SharedHelper.getKey(this, LoginActivity.USER_ID), String.valueOf(currentWorkTime))
                .enqueue(new Callback<UserWorkTimeResponse>() {
                    @Override
                    public void onResponse(Call<UserWorkTimeResponse> call, Response<UserWorkTimeResponse> response) {
                        if (response.body() != null) {

                            Log.d("dddddddddd", "onResponse: " + response.body().getData());
                            Log.d("dddddddddd", "time after end: " + currentWorkTime);
                            setPldTimeWorkZero();
                            pauseServiceTimer();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserWorkTimeResponse> call, Throwable t) {
                        saveWorkedTime();
                        pauseServiceTimer();
                        Log.d("dddddddddd", "onResponse: " + t.getMessage());
                    }
                });
        safelyDispose(networkDisposable, internetDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    LastCallDetails getLastCallDetails() {
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
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

        Log.d("callEndedcallEnded", "callEnded: ");
        if (hasInternet && connectionType.equals("WIFI"))
            uploadVoices();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        LastCallDetails callDetails = getLastCallDetails();
                        Log.d("callDetails", "callDetails: " + callDetails.getType());
                        if (callDetails.getType().equals("MISSED") || callDetails.getType().equals("REJECTED")) {
                            if (callDetails.getActiveId().equals(SharedHelper.getKey(getApplicationContext(),
                                    "activated_sub_id"))) {
                                BaseClient.getBaseClient().create(Api.class).uploadPhone(SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN), callDetails.getPhone())
                                        .enqueue(new Callback<UploadPhoneResponse>() {
                                            @Override
                                            public void onResponse(Call<UploadPhoneResponse> call, Response<UploadPhoneResponse> response) {
                                                if (response.body().getResponse().equals("Success")) {
                                                    Toast.makeText(OrderActivity.this, "تم ارسال رقم  " + callDetails.getPhone() + " المكالمة الفائتة الى السيرفر", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<UploadPhoneResponse> call, Throwable t) {

                                            }
                                        });

                            }

                        } else if (callDetails.getType().equals("OUTGOING")) {

                            if (getAutoValue())
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
                                    minutesUsage(callDetails.getDuration());
                                    updateOrderTimer();
                                }
                        }


                    }
                });

            }
        }, 1000);

    }

    private boolean getAutoValue() {
        SharedPreferences auto = PreferenceManager.getDefaultSharedPreferences(this);
        return auto.getBoolean("autoUpdate", false);
    }


    @SuppressLint("ApplySharedPref")
    private void minutesUsage(String seconds) {
        Log.d("minutesUsage", "minutesUsage: call time seconds" + seconds);
        int totalSeconds = 149 * 59;
        int minutes = totalSeconds / 59;
        int remaining = 0;
        if ((totalSeconds % 59) > 0) {
            remaining = 1;
        }
        Log.d("minutesUsage", "minutesUsage: call time minutes" + minutes);
        Log.d("minutesUsage", "minutesUsage: call time remaining" + remaining);
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        float oldUsage = pref1.getFloat("cards_usage", 0f);
        float currentUsage = (float) (minutes + remaining);
        float newUsage = oldUsage + currentUsage;
        Log.d("minutesUsage", "minutesUsage: call time all m" + newUsage);
        pref1.edit().putFloat("cards_usage", newUsage).commit();
        Log.d("minutesUsage", "minutesUsage: " + pref1.getFloat("cards_usage", 0f));

    }

    private void uploadVoices() {
        callDetailsList = databaseManager.getAllDetails();
        for (CallDetails call : callDetailsList) {
            if (call.getUploaded().equals("not_yet")) {

                String path = Environment.getExternalStorageDirectory() + "/MyRecords/" + call.getDate() + "/" + call.getNum() + "_" + call.getTime1() + ".mp4";
                File file = new File(path);
                RequestBody surveyBody = RequestBody.create(MediaType.parse("audio/*"), file);
                MultipartBody.Part image = MultipartBody.Part.createFormData("voice_note", file.getName(), surveyBody);
                RequestBody title1 = RequestBody.create(MediaType.parse("text/plain"), order_ud);
                RequestBody token = RequestBody.create(MediaType.parse("text/plain"), SharedHelper.getKey(this, LoginActivity.TOKEN));
                BaseClient.getBaseClient().create(Api.class).uploadVoice(token, title1, image).enqueue(new Callback<UploadVoiceResponse>() {
                    @Override
                    public void onResponse(Call<UploadVoiceResponse> call2, Response<UploadVoiceResponse> response) {
                        databaseManager.updateCallDetails(call);
                        Log.d("caaaaaaaaaaaaaal", "onResponse: " + response.body().getInfo());
                    }

                    @Override
                    public void onFailure(Call<UploadVoiceResponse> call, Throwable t) {
                        Log.d("caaaaaaaaaaaaaal", "onFailure: " + t.getMessage());

                    }
                });
            }
        }
    }

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

    @Override
    public void orderSentGetNewOrder() {
        Toast.makeText(this, "تم ارسال الطلب", Toast.LENGTH_SHORT).show();
        getFirstOrder();
    }

    public void onConnectionLost() {
        Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
        startActivityForResult(intent, 8523);
        Toast.makeText(this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_SHORT).show();
       // finishTheWorkNow();
    }

    public void onConnectionFound() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    public void showLabel(View view) {
        LinearLayout linearLayout = findViewById(view.getId());

        RunAnimation(linearLayout.getChildAt(0).getId(), linearLayout.getChildAt(1).getId());
    }

    private void RunAnimation(int id1, int id2) {

        TextView tv = (TextView) findViewById(id1);
        FrameLayout bg = (FrameLayout) findViewById(id2);

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

        if (value != null) {
            valueTxv.setText(value.toPlainString());
            signChk.setEnabled(value.compareTo(BigDecimal.ZERO) != 0);
        }

    }

    void setUpProgressBar() {
        int[] colors = new int[4];
        colors[0] = getResources().getColor(R.color.red);
        colors[1] = getResources().getColor(R.color.blue);
        colors[2] = getResources().getColor(R.color.material_light_yellow_A100);
        colors[3] = getResources().getColor(R.color.green);
        Drawable progressDrawable = new ChromeFloatingCirclesDrawable.Builder(this)
                .colors(colors)
                .build();
        Rect bounds = progressBar.getIndeterminateDrawable().getBounds();
        progressBar.setIndeterminateDrawable(progressDrawable);
        progressBar.getIndeterminateDrawable().setBounds(bounds);

    }

    private void openRecords() {
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        pref1.edit().putBoolean("switchOn", true).apply();
    }

    private void closeRecords() {
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        pref1.edit().putBoolean("switchOn", false).apply();
    }

    private void saveWorkedTime() {
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        long oldWork = pref1.getLong("timeWork", 0);
        long newWorkedTime = oldWork + timeWork;
        Log.d("saveWorkedTime", "saveWorkedTime: " + newWorkedTime);
        pref1.edit().putLong("timeWork", newWorkedTime).apply();
    }

    private long getNotSavedWrokTime() {
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        return pref1.getLong("timeWork", 0);
    }

    private void setPldTimeWorkZero() {
        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(this);
        pref1.edit().putLong("timeWork", 0).apply();
        Log.d("saveWorkedTime", "setPldTimeWorkZero: ");
    }

    List<ProductForSpinner> singleProducts;
    List<String> singleProductIds;
    String singleProductId;

    @BindView(R.id.product)
    Spinner singleProductSpinner;

    private void setSingleProducts(String selection) {
        singleProducts = new ArrayList<>();
        singleProductIds = new ArrayList<>();
        for (ProductData product : allProducts.getProducts()) {
            singleProducts.add(new ProductForSpinner(product.getProduct_name(), product.getProduct_image(), product.getProduct_id()));
            singleProductIds.add(product.getProduct_id());
        }

        ArrayAdapter<String> myAdapter = new ProductSpinnerAdapter(
                this, singleProducts);
        singleProductSpinner.setAdapter(myAdapter);
        singleProductSpinner.setSelection(singleProductIds.indexOf(selection));
        singleProductId = selection;
        singleProductSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                singleProductId = singleProductIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    List<ProductForSpinner> products;
    Spinner productSpinner;
    String productId;
    EditText productNo;
    RobotoTextView addProductBtn;


    public void showNewProductDialog(View view) {
        Api api = BaseClient.getBaseClient().create(Api.class);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_add_rpoduct_dialog);
        productSpinner = dialog.findViewById(R.id.product_spinner);
        productNo = dialog.findViewById(R.id.product_no);

        products = new ArrayList<>();
        for (ProductData product : allProducts.getProducts()) {
            products.add(new ProductForSpinner(product.getProduct_name(), product.getProduct_image(), product.getProduct_id()));
        }

        ArrayAdapter<String> myAdapter = new ProductSpinnerAdapter(
                this, products);
        productSpinner.setAdapter(myAdapter);

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productId = allProducts.getProducts().get(position).getProduct_id();
                // Toast.makeText(getActivity(), ""+productId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        addProductBtn = dialog.findViewById(R.id.add_product);
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                api.addProduct(
                        SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN),
                        order_ud,
                        productId,
                        item_no.getText().toString()
                ).enqueue(new Callback<DeleteAddProductResponse>() {
                    @Override
                    public void onResponse(Call<DeleteAddProductResponse> call, Response<DeleteAddProductResponse> response) {
                        Log.d("DeleteAddProduct", "onResponse: " + response.body().getCode());
                        if (response.body().getCode().equals("1200")) {
                            Toast.makeText(OrderActivity.this, "تم اضافة المنتج", Toast.LENGTH_SHORT).show();
                            getFirstOrder();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<DeleteAddProductResponse> call, Throwable t) {
                        Log.d("DeleteAddProduct", "onFailure: " + t.getMessage());
                        dialog.dismiss();
                        getFirstOrder();
                    }
                });
            }
        });

        dialog.show();
    }


}

