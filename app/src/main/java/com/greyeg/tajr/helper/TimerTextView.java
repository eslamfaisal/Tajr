package com.greyeg.tajr.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@SuppressLint("AppCompatCustomView")
public class TimerTextView extends TextView {

    private static final int DEFAULT_INTERVAL = 1000;

    private Timer timer = new Timer();
    private long interval = DEFAULT_INTERVAL;
    private boolean isCanceled = false;
    public long seconds;
    public long starterTime = 0;
    public TimerTextView(Context context) {
        super(context);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }

    @Override protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        if (VISIBLE == visibility) {
//            startTimer();
//        } else {
//            stopTimer();
//        }
    }


    public void setInterval(long interval) {
        if (interval >= 0) {
            this.interval = interval;
            stopTimer();
            startTimer();
        }
    }

    public void setEndTime(long endTime) {
        if (endTime >= 0) {

            stopTimer();
            startTimer();
        }
    }

    public void setStarterTime(long starterTime) {
        this.starterTime = starterTime;
    }

    public void startTimer() {
        if (starterTime==0) return;
        if (isCanceled) {
            timer = new Timer();
            isCanceled = false;
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                if (null == getHandler()) {
                    return;
                }
                getHandler().post(new Runnable() {
                    @Override public void run() {
                        seconds +=1;
                        setText(getDurationBreakdown( System.currentTimeMillis()-starterTime));
                    }
                });
            }
        }, 0, interval);
    }

    public void stopTimer() {
        timer.cancel();
        starterTime =0;
        isCanceled = true;
      //  setText(getDurationBreakdown( 0));
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
}