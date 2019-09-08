package com.greyeg.tajr.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.greyeg.tajr.MainActivity;
import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.Subscriber;
import com.greyeg.tajr.models.SubscriberInfo;
import com.greyeg.tajr.server.BaseClient;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubbleService extends Service {

    private WindowManager mWindowManager;
    private View bubbleView;
    private View deleteView;
    WindowManager.LayoutParams params;
    View expandedView;
    public static String userName=null;
    public  String userID=null;
    private static final int CLICK_ACTION_THRESHOLD = 2;
    private float startX;
    private float startY;
    public static boolean isRunning=false;
    private   boolean deleteViewAdded=false;
    private Handler handler;
    private Runnable runnable;

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
        deleteView = LayoutInflater.from(this).inflate(R.layout.bubble_delete, null,false);
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

        Log.d("SCREEEN", "width: "+ MainActivity.screenWidth+"     "+MainActivity.screenHeight);

        bubbleView.findViewById(R.id.send_broadcast)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(BubbleService.this, "send broadcast", Toast.LENGTH_SHORT).show();
                    }
                });

        bubbleView.findViewById(R.id.register_user)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(BubbleService.this, "register user", Toast.LENGTH_SHORT).show();
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
                        if (userName==null){
                            bubbleView.findViewById(R.id.gotUserName)
                                    .setBackgroundResource(R.drawable.circle_gray);
                            bubbleView.findViewById(R.id.gotUserId)
                                    .setBackgroundResource(R.drawable.circle_gray);
                            return true;
                        }
                        else{
                            bubbleView.findViewById(R.id.gotUserName)
                                    .setBackgroundResource(R.drawable.circle_green);
                            if (userID==null){
                                getUserId("Ahmed Khaled");
                            }else {
                                if (expandedView.getVisibility()==View.GONE)
                                    expandedView.setVisibility(View.VISIBLE);
                                else
                                    expandedView.setVisibility(View.GONE);
                            }

                        }

                    }


                    return true;

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);

                    // bubble is in the bottom and delete bubble must be shown
                    if (event.getRawY()/MainActivity.screenHeight>.75){
                        Log.d("DELETEE", "is delete added "+deleteViewAdded);

                        if (!deleteViewAdded){
                            mWindowManager.addView(deleteView,getDeleteViewParams());
                            deleteViewAdded=true;
                        }
                    }else{
                        if (deleteViewAdded){
                        mWindowManager.removeView(deleteView);
                        deleteViewAdded=false;
                        }
                        //Log.d("DELETEE", "before delete: "+event.getRawY()+" "+MainActivity.screenHeight) ;

                    }


                    if (event.getRawY()>MainActivity.screenHeight-80
                            &&event.getRawX()>MainActivity.screenWidth/2-20
                            &&event.getRawX()<MainActivity.screenWidth/2+80
                            )
                    {
                        Log.d("DELETEE", "must delete: "+MainActivity.screenHeight);
                        if (deleteViewAdded){
                            mWindowManager.removeView(bubbleView);
                            mWindowManager.removeView(deleteView);
                        deleteViewAdded=false;
                        }
                        stopSelf();
                        isRunning=false;


                    }

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

    private WindowManager.LayoutParams getDeleteViewParams(){
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
        params.x = MainActivity.screenWidth/2-60;
        params.y = MainActivity.screenHeight-50;

        return params;
    }

    private void getUserId(String userName){
        String token= SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN);
        Log.d("SUBSCRIPERR", "token: "+token);
        startFlasher();
        BaseClient.getService()
                .getSubscriberInfo(token,userName)
                .enqueue(new Callback<SubscriberInfo>() {
                    @Override
                    public void onResponse(Call<SubscriberInfo> call, Response<SubscriberInfo> response) {
                        stopFlasher();
                        SubscriberInfo subscriberInfo=response.body();
                        if (response.isSuccessful()&&subscriberInfo!=null){
                            Log.d("SUBSCRIPERR","subscriper "+response.body().getData());
                            ArrayList<Subscriber> subscribersData =subscriberInfo.getSubscribers_data();
                            if(subscribersData==null||subscribersData.isEmpty()){
                                Toast.makeText(BubbleService.this,
                                        "there is a problem fetching user data"
                                        , Toast.LENGTH_SHORT).show();
                            }else if (subscribersData.size()==1){
                                userID=subscribersData.get(0).getPsid();
                            }else {


                            }

                        }
                        else {
                            try {
                                Log.d("SUBSCRIPERR","code "+response.code()+" error "+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d("SUBSCRIPERR","error");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SubscriberInfo> call, Throwable t) {
                        stopFlasher();
                        Log.d("SUBSCRIPERR", "onFailure: "+t.getMessage());
                    }
                });
    }

    boolean run=false;
    private void startFlasher(){

        handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("SUBSCRIPERR","run ");
                if (run)
                    bubbleView.findViewById(R.id.gotUserId)
                            .setBackgroundResource(R.drawable.circle_green);
                else
                    bubbleView.findViewById(R.id.gotUserId)
                            .setBackgroundResource(R.drawable.circle_gray);

                handler.postDelayed(this,300);
                run=!run;
            }
        });

//        bubbleView.findViewById(R.id.gotUserId)
//                .setBackgroundResource(R.drawable.circle_green);
    }

    private void stopFlasher(){
        handler.removeCallbacksAndMessages(null);
        //handler.removeCallbacks(runnable);
        bubbleView.findViewById(R.id.gotUserId)
                .setBackgroundResource(R.drawable.circle_green);
    }



    }
