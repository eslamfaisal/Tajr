package com.greyeg.tajr.callrecorder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.axet.androidlibrary.widgets.OptimizationPreferenceCompat;
import com.greyeg.tajr.callrecorder.app.CallApplication;


public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        OptimizationPreferenceCompat.setBootInstallTime(context, CallApplication.PREFERENCE_BOOT, System.currentTimeMillis());
        RecordingService.startIfEnabled(context);
    }
}
