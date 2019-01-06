package com.greyeg.tajr.call_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class IncomingCallInterceptor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String callingSIM = "";
        Bundle bundle = intent.getExtras();
        callingSIM =String.valueOf(bundle.getInt("simId", -1));
        if(callingSIM == "0"){
            Log.d("ggggggggggggggg", "onReceive: 0");
        }
        else if(callingSIM =="1"){
            Log.d("ggggggggggggggg", "onReceive: 1");
        }
    }
}