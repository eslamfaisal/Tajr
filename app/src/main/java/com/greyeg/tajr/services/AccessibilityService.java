package com.greyeg.tajr.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.greyeg.tajr.MainActivity;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    public static final String TAG="ACCESSIBLILTYY";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //Log.d(TAG, "onAccessibilityEvent: "+accessibilityEvent.toString());

        if (accessibilityEvent.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        &&accessibilityEvent.getPackageName().equals("com.facebook.pages.app"))
        {
            checkOverlayPermission();
        }


    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: ");
    }

    private void checkOverlayPermission(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&!Settings.canDrawOverlays(getApplicationContext())){
                Intent intent=new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {
                showBubble();
            }

    }

    void showBubble(){
        if (!BubbleService.isRunning)
        startService(new Intent(this, BubbleService.class));
    }


}
