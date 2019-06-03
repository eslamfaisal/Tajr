package com.greyeg.tajr.order;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.calc.CalcDialog;
import com.greyeg.tajr.helper.GuiManger;
import com.greyeg.tajr.order.fragments.CurrentOrderFragment;
import com.greyeg.tajr.order.fragments.MissedCallFragment;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewOrderActivity extends AppCompatActivity {

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
//            if (CurrentOrderData.getInstance().getCurrentOrderResponse().getOrder() != null)
//                callClient();
        } else if (id == R.id.mic_mode) {
            modifyMic();
        }
        return false;
    };
    private Timer workTimer;
    private int DIALOG_REQUEST_CODE = 25;
    private CalcDialog calcDialog;
    private MissedCallFragment missedCallFragment;

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
}
