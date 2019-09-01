package com.greyeg.tajr.callrecorder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.github.axet.androidlibrary.preferences.OptimizationPreferenceCompat;
import com.greyeg.tajr.callrecorder.app.CallApplication;

public class OnUpgradeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        OptimizationPreferenceCompat.setBootInstallTime(context, CallApplication.PREFERENCE_INSTALL, System.currentTimeMillis());
        RecordingService.startIfEnabled(context);
    }
}
