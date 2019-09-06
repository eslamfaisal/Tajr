package com.greyeg.tajr.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.greyeg.tajr.R;

public class BubbleService extends Service {

    private WindowManager mWindowManager;
    private View bubbleView;
    WindowManager.LayoutParams params;
    View expandedView;
    private static final int CLICK_ACTION_THRESHOLD = 2;
    private float startX;
    private float startY;
    public static boolean isRunning=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning=true;

        bubbleView = LayoutInflater.from(this).inflate(R.layout.bubble, null,false);
        View collapsedView=bubbleView.findViewById(R.id.collapsed_bubble);
        expandedView=bubbleView.findViewById(R.id.expanded_bubble);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }else{
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(bubbleView, params);


        collapsedView.setOnTouchListener(onTouchListener);
        expandedView.setOnTouchListener(onTouchListener);

        collapsedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(BubbleService.this, "hey", Toast.LENGTH_SHORT).show();
                //expandedView.setVisibility(View.VISIBLE);
            }
        });

    }

    View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    initialX = params.x;
                    initialY = params.y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    startX = event.getX();
                    startY = event.getY();
                    return true;

                case MotionEvent.ACTION_UP:

                    float endX = event.getX();
                    float endY = event.getY();
                    if (isAClick(startX, endX, startY, endY)) {
                        Log.d("TOUCHHH", "onTouch: ");
                        if (expandedView.getVisibility()==View.GONE)
                        expandedView.setVisibility(View.VISIBLE);
                            else
                                expandedView.setVisibility(View.GONE);
                    }




                    return true;

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);


                    mWindowManager.updateViewLayout(bubbleView, params);
                    return true;
            }
            return false;
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bubbleView != null) mWindowManager.removeView(bubbleView);
        isRunning=false;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }
}
