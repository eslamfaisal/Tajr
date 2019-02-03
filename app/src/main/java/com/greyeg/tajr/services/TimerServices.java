package com.greyeg.tajr.services;

import android.app.IntentService;
import android.content.Intent;

import com.greyeg.tajr.MainActivity;
import com.greyeg.tajr.activities.OrderActivity;

import java.util.Timer;
import java.util.TimerTask;

public class TimerServices extends IntentService {

    Timer timer;




    public TimerServices() {
        super("TimerServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (!stoped){
//                    OrderActivity.timeWork += 1;
//                }
//
//            }
//        }, 0, 1000);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

    }
}
