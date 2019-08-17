package com.greyeg.tajr.order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.calc.CalcDialog;
import com.greyeg.tajr.helper.CurrentCallListener;
import com.greyeg.tajr.helper.GuiManger;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.LastCallDetails;
import com.greyeg.tajr.models.UploadPhoneResponse;
import com.greyeg.tajr.models.UploadVoiceResponse;
import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.order.fragments.CurrentOrderFragment;
import com.greyeg.tajr.order.fragments.MissedCallFragment;
import com.greyeg.tajr.records.CallDetails;
import com.greyeg.tajr.records.CallsReceiver;
import com.greyeg.tajr.records.CommonMethods;
import com.greyeg.tajr.records.DatabaseManager;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewOrderActivity extends AppCompatActivity implements CurrentCallListener {

    private final String TAG = "NewOrderActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    // call controller view
    boolean micMute;
    // work time
    @BindView(R.id.timer)
    TextView timerTv;
    CurrentOrderFragment currentOrderFragment;
    private Menu callControllerMenu;
    private MenuItem micMode;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        int id = item.getItemId();
        if (id == R.id.end_call) {
            try {
                endCAll();
            } catch (Exception e) {
                Log.d(TAG, "cannot end the call: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (id == R.id.call_client) {
            if (CurrentOrderData.getInstance().getCurrentOrderResponse().getOrder() != null)
                callClient();
        } else if (id == R.id.mic_mode) {
            modifyMic();
        }
        return false;
    };
    private Timer workTimer;
    private int DIALOG_REQUEST_CODE = 25;
    private CalcDialog calcDialog;
    private MissedCallFragment missedCallFragment;
    private DatabaseManager databaseManager;

    //GUIManger Methods
    public static void update() {
        GuiManger.getInstance().getFragmentManager().beginTransaction().addToBackStack("")
                .replace(R.id.Handle_Frame, GuiManger.getInstance().getcurrFragment(), null).commit();
    }

    public static void
    finishWork() {
        GuiManger.getInstance().getActivity().finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        CallsReceiver.setCurrentCallListener(this);
        databaseManager = new DatabaseManager(this);
        openRecords();
        initToolBar();
        GuiManger.getInstance().setActivity(this);
        GuiManger.getInstance().setFragmentManager(getSupportFragmentManager());
        currentOrderFragment = new CurrentOrderFragment();
        missedCallFragment = new MissedCallFragment();
        GuiManger.getInstance().setcurrFragment(currentOrderFragment);

        initCallController();
    }

    private void initCallController() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        callControllerMenu = navigation.getMenu();

        micMode = callControllerMenu.findItem(R.id.mic_mode);

        if (!micMute) {

            micMode.setIcon(R.drawable.ic_mic_none_black_24dp);
            micMode.setTitle(getString(R.string.mute_mic));

        } else {

            micMode.setIcon(R.drawable.ic_mic_off_black_24dp);
            micMode.setTitle(getString(R.string.turn_on_mic));

        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        timerTv.setTag(0L);
    }

    private void initTimer() {
        workTimer = new Timer();
        workTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long count = (long) timerTv.getTag() + 1;
                runOnUiThread(() -> {
                    timerTv.setText(getDurationBreakdown(count * 1000));
                });
                timerTv.setTag(count);
                saveWorkedTime();
            }
        }, 0, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTimer();
    }

    public String getDurationBreakdown(long diff) {
        long millis = diff;
        if (millis < 0) {
            return "00:00:00";
        }
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);


        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
        //return "${getWithLeadZero(hours)}:${getWithLeadZero(minutes)}:${getWithLeadZero(seconds)}"
    }

    @Override
    protected void onPause() {
        super.onPause();
        workTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        if (GuiManger.getInstance().getcurrFragment() instanceof CurrentOrderFragment) {
            finish();
        } else
            super.onBackPressed();
    }

    private void endCAll() throws Exception {
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        Class c = Class.forName(tm.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
        c = Class.forName(telephonyService.getClass().getName()); // Get its class
        m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
        m.setAccessible(true); // Make it accessible
        m.invoke(telephonyService); // invoke endCall()
    }

    private void modifyMic() {

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

    private void checkMic() {
        if (!micMute) {
            micMode.setIcon(R.drawable.ic_mic_none_black_24dp);
            micMode.setTitle(getString(R.string.mute_mic));

        } else {
            micMode.setIcon(R.drawable.ic_mic_off_black_24dp);
            micMode.setTitle(getString(R.string.turn_on_mic));
        }
    }

    public void callClient() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + CurrentOrderData.getInstance().getCurrentOrderResponse().getOrder().getPhone1()));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calc) {
            showCalculatoe();
        } else if (id == R.id.finish_work) {
            finishWork();
        } else if (id == R.id.show_missed_call) {
            if (GuiManger.getInstance().getcurrFragment() instanceof MissedCallFragment) {
                GuiManger.getInstance().setcurrFragment(currentOrderFragment);
            } else if (GuiManger.getInstance().getcurrFragment() instanceof CurrentOrderFragment) {
                GuiManger.getInstance().setcurrFragment(missedCallFragment);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCalculatoe() {
        calcDialog = CalcDialog.newInstance(DIALOG_REQUEST_CODE);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("calc_dialog") == null) {
            calcDialog.show(fm, "calc_dialog");
        }
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
        long newWorkedTime = oldWork + 1;
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

    private void saveWorkTime() {
        long currentWorkTime = getNotSavedWrokTime() + Integer.valueOf(timerTv.getTag().toString());
        BaseClient.getBaseClient().create(Api.class).userWorkTime(SharedHelper.getKey(this, LoginActivity.TOKEN),
                String.valueOf(currentWorkTime),
                CurrentOrderData.getInstance().getCurrentOrderResponse().getUserId())
                .enqueue(new Callback<UserWorkTimeResponse>() {
                    @Override
                    public void onResponse(Call<UserWorkTimeResponse> call, Response<UserWorkTimeResponse> response) {
                        if (response.body() != null) {

                            Log.d("userWorkTime", "onResponse: " + response.body().getData());
                            Log.d("userWorkTime", "time after end: " + currentWorkTime);
                            setPldTimeWorkZero();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserWorkTimeResponse> call, Throwable t) {

                        Log.d("userWorkTime", "onResponse: " + t.getMessage());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelNotification();
        closeRecords();
        saveWorkTime();
    }

    public void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(5);
    }


    private LastCallDetails getLastCallDetails() {
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
        String phoneNumber = null;
        String callDuration2 = null;
        String activatedNum = null;
        String calType = null;
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
    public void callEnded(int serialNumber, String phoneNumber) {
        Log.d("callEndedcallEnded", "callEnded: ");

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
                                BaseClient.getBaseClient().create(Api.class).missedCall(SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN), callDetails.getPhone())
                                        .enqueue(new Callback<UploadPhoneResponse>() {
                                            @Override
                                            public void onResponse(Call<UploadPhoneResponse> call, Response<UploadPhoneResponse> response) {
                                                if (response.body().getResponse().equals("Success")) {
                                                    Toast.makeText(NewOrderActivity.this, "تم ارسال رقم  " + callDetails.getPhone() + " المكالمة الفائتة الى السيرفر", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<UploadPhoneResponse> call, Throwable t) {

                                            }
                                        });

                            }

                        } else if (callDetails.getType().equals("OUTGOING")) {

                            if (callDetails.getDuration().equals("0")) {
                                Log.d(TAG, "run: add call to database");
                                new DatabaseManager(getApplicationContext()).addCallDetails(new CallDetails(serialNumber,
                                        phoneNumber, new CommonMethods().getTIme(), new CommonMethods().getDate(), "not_yet", callDetails.getDuration()));

                                minutesUsage(callDetails.getDuration());
                            }
                        }


                    }
                });

            }
        }, 1000);

        uploadVoices();
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
        List<CallDetails> callDetailsList = databaseManager.getAllDetails();
        for (CallDetails call : callDetailsList) {
            if (call.getUploaded().equals("not_yet")) {

                String path = Environment.getExternalStorageDirectory() + "/MyRecords/" + call.getDate() + "/" + call.getNum() + "_" + call.getTime1() + ".mp4";
                File file = new File(path);
                RequestBody surveyBody = RequestBody.create(MediaType.parse("audio/*"), file);
                MultipartBody.Part image = MultipartBody.Part.createFormData("voice_note", file.getName(), surveyBody);
                RequestBody title1 = RequestBody.create(MediaType.parse("text/plain"), CurrentOrderData.getInstance().getCurrentOrderResponse().getOrder().getId());
                RequestBody duration = RequestBody.create(MediaType.parse("text/plain"), call.getDuration());
                RequestBody token = RequestBody.create(MediaType.parse("text/plain"), SharedHelper.getKey(this, LoginActivity.TOKEN));

                BaseClient.getBaseClient().create(Api.class).uploadVoice(token, title1, duration, image).enqueue(new Callback<UploadVoiceResponse>() {
                    @Override
                    public void onResponse(Call<UploadVoiceResponse> call2, Response<UploadVoiceResponse> response) {
                        databaseManager.updateCallDetails(call);

                    }

                    @Override
                    public void onFailure(Call<UploadVoiceResponse> call, Throwable t) {
                        Log.d("caaaaaaaaaaaaaal", "onFailure: " + t.getMessage());

                    }
                });
            }
        }
    }

}
