package com.greyeg.tajr.helper;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimeCalculator {

    private static TimeCalculator timeCalculator;
    private static Timer timer;
    private static boolean running=false;


    public static TimeCalculator getInstance() {
        return timeCalculator==null?new TimeCalculator():timeCalculator;
    }

    private TimeCalculator() {
    }


    public  void startTimer(){
        if (running) return;
        timer=new Timer();
        Log.d("TIMERCALCC", "timer started: ");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d("TIMERCALCC", "run ");

            }
        },0,1000);

        running=true;

    }

    public  void stopTimer(){
        Log.d("TIMERCALCC", "timer stopped: ");
        if (timer!=null){
            timer.cancel();
            timer.purge();
            timer=null;
        }
        running=false;
    }

    public boolean isRunning() {
        return running;
    }
}
