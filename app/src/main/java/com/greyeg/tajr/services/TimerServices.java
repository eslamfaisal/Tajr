package com.greyeg.tajr.services;

import android.app.IntentService;
import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;

public class TimerServices extends IntentService {

    Timer timer;
    public static long timeWork;

    public static boolean stoped = false;

    public TimerServices() {
        super("TimerServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!stoped)
                    timeWork += 1;
            }
        }, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
