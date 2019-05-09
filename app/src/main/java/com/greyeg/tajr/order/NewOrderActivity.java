package com.greyeg.tajr.order;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.GuiManger;
import com.greyeg.tajr.order.fragments.CurrentOrderFragment;
import com.greyeg.tajr.order.models.CurrentOrderResponse;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewOrderActivity extends AppCompatActivity {

    private final String TAG = "NewOrderActivity";

    @BindView(R.id.timer)
    TextView timerTv;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    //GUIManger Methods
    public static void update() {
        GuiManger.getInstance().getFragmentManager().beginTransaction().addToBackStack("")
                .replace(R.id.Handle_Frame, GuiManger.getInstance().getcurrFragment(), null).commitAllowingStateLoss();
    }

    public static void
    finishWork() {
        GuiManger.getInstance().getActivity().finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        initToolBar();
        GuiManger.getInstance().setActivity(this);
        GuiManger.getInstance().setFragmentManager(getSupportFragmentManager());
        GuiManger.getInstance().setcurrFragment(new CurrentOrderFragment());

    }

    private void initToolBar(){
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        timerTv.setTag(0L);
    }
    private Timer workTimer;

    private void initTimer(){
        workTimer= new Timer();
        workTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long count =(long) timerTv.getTag()+1;
                runOnUiThread(() -> {
                    timerTv.setText(getDurationBreakdown(count*1000));
                });
                timerTv.setTag(count);
            }
        },0,1000);
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
}
