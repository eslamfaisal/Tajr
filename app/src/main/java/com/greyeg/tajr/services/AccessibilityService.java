package com.greyeg.tajr.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.greyeg.tajr.MainActivity;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    public static final String TAG="ACCESSIBLILTYY";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent: "+accessibilityEvent.toString());

        if (accessibilityEvent.getEventType()==AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        &&accessibilityEvent.getPackageName().equals("com.facebook.pages.app"))
        {
            //Log.d(TAG, "onAccessibilityEvent: "+"TYPE_WINDOW_STATE_CHANGED");

            checkOverlayPermission();
            String userName=getUserName();
                BubbleService.userName=userName;
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
        Log.d(TAG, "checkOverlayPermission: ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&!Settings.canDrawOverlays(getApplicationContext())){
                Intent intent=new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {
                Log.d(TAG, "show bubble from accessibility:" );
                showBubble();
            }



    }

    void showBubble(){
        if (!BubbleService.isRunning)
        startService(new Intent(this, BubbleService.class));
    }


    private String getUserName(){
        AccessibilityNodeInfo root =getRootInActiveWindow();


        if (root.getChildCount()>1 &&root.getChild(1)!=null
        &&root.getChild(0).getClassName().equals("android.widget.ImageView")
        &&root.getChild(1).getClassName().equals("android.view.ViewGroup")
        ) {
            AccessibilityNodeInfo curent = root
                    .getChild(1);
            for (int i = 0; i < curent.getChildCount(); i++) {
                Log.d(TAG, curent.getChild(i).getClassName()
                        + " " + curent.getText() + "  "
                        + " " + curent.getChildCount());


                Log.d(TAG, "child : "+curent.getChild(0).getText());
                AccessibilityNodeInfo username=curent.getChild(0);
                if (username.getText()!=null)
                    return username.getText().toString();
            }

            Log.d(TAG, "///////////////////////////////////");
        }else{
            Log.d(TAG, "getUserName: not found");
        }
       return null;
    }


}
