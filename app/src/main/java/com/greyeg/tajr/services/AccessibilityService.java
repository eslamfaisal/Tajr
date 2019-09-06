package com.greyeg.tajr.services;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    public static final String TAG="ACCESSIBLILTYY";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //Log.d(TAG, "onAccessibilityEvent: "+accessibilityEvent.toString());

        if (accessibilityEvent.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        &&accessibilityEvent.getPackageName().equals("com.facebook.pages.app"))
        {

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
}
