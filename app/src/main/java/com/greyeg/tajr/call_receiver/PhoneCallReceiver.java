package com.greyeg.tajr.call_receiver;

/**
 * Created by jeet on 24/12/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.greyeg.tajr.activities.EmptyCallActivity;
import com.greyeg.tajr.helper.CurrentCallListener;

import java.util.Date;


public abstract class PhoneCallReceiver extends BroadcastReceiver {

    public static String myCallNumber;
    public static boolean enCallFromMe;
    public static CurrentCallListener currentCallListener;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    public static void setCurrentCallListener(CurrentCallListener listener) {
        currentCallListener = listener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                final Intent intent2 = new Intent(context, EmptyCallActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(intent2);
                    }
                }, 1000);

            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                //  String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //  String sub = intent.getExtras().getString(TelephonyManager.EXTRA_SUBSCRIPTION_ID);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                if (stateStr.equals("RINGING")) {
                    // هنا لو الحالة بيرن هنعمل الماكلمة الفيتة

                } else if (stateStr.equals("IDLE")) {

//                    if (currentCallListener != null) {
//                       currentCallListener.callEnded();
//                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {

            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }

            case TelephonyManager.CALL_STATE_IDLE:
                if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
        }
        lastState = state;
    }


}
