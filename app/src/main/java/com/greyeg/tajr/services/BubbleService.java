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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.MainActivity;
import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.adapters.BotBlocksAdapter;
import com.greyeg.tajr.helper.ScreenHelper;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.BotBlock;
import com.greyeg.tajr.models.BotBlocksResponse;
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
    View botBlocksDialog;
    public static String userName=null;
    public  String userID=null;
    private static final int CLICK_ACTION_THRESHOLD = 0;
    private float startX;
    private float startY;
    public static boolean isRunning=false;
    private   boolean deleteViewAdded=false;
    private Handler handler;
    private int width,height;
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

        width= ScreenHelper.getScreenDimensions(getApplicationContext())[0];
        height= ScreenHelper.getScreenDimensions(getApplicationContext())[1];

        Log.d("SCREEEEEENw", "onCreate: "+width);

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

        Log.d("SCREEEN", "width: "+ width+"     "+height);

        bubbleView.findViewById(R.id.send_broadcast)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBotBlocks();
                        Toast.makeText(BubbleService.this, "send broadcast", Toast.LENGTH_SHORT).show();
                    }
                });

        bubbleView.findViewById(R.id.register_order)
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
                    if (event.getRawY()/height>.75){
                        Log.d("DELETEE", "is delete added "+deleteViewAdded);

                        if (!deleteViewAdded){
                            mWindowManager.addView(deleteView
                                    ,getViewParams(
                                            width/2-60
                                            ,height-50
                                    ,-1,-1));
                            deleteViewAdded=true;
                        }
                    }else{
                        if (deleteViewAdded){
                        mWindowManager.removeView(deleteView);
                        deleteViewAdded=false;
                        }

                    }


                    if (event.getRawY()>height-80
                            &&event.getRawX()>width/2-20
                            &&event.getRawX()<width/2+80
                            )
                    {
                        Log.d("DELETEE", "must delete: h"+height);
                        if (deleteViewAdded){
                            mWindowManager.removeView(bubbleView);
                            mWindowManager.removeView(deleteView);
                        deleteViewAdded=false;
                        bubbleView=null;
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

    View.OnTouchListener dialogTouchListener =new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:


                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;


                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);


                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(botBlocksDialog, params);
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

    // method for detecting click in touch listener
    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
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

    private void getBotBlocks(){
        String token=SharedHelper.getKey(getApplicationContext(),LoginActivity.TOKEN);
        BaseClient.getService()
                .getBotBlocks(token)
                .enqueue(new Callback<BotBlocksResponse>() {
                    @Override
                    public void onResponse(Call<BotBlocksResponse> call, Response<BotBlocksResponse> response) {
                        BotBlocksResponse botBlocksResponse=response.body();
                        if (response.isSuccessful()&&botBlocksResponse!=null){
                            setupBotBlocksDialog(botBlocksResponse);


                            //Log.d("BOTBLOKSS", "onResponse: "+botBlocksResponse.getBlocks().getDefault().get(0).getName());
                        }else{
                            Toast.makeText(BubbleService.this,
                                    "Error getting Bot Blocks \n response code "+response.code()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BotBlocksResponse> call, Throwable t) {
                        Log.d("BOTBLOKSS", "onFailure: "+t.getMessage());
                        Toast.makeText(BubbleService.this,
                                "failed to get Blocks \n "+t.getMessage()
                                , Toast.LENGTH_SHORT).show();

                    }
                });
    }



    private void showBotBlocks(ArrayList<BotBlock> botBlocks, RecyclerView recyclerView){
        BotBlocksAdapter botBlocksAdapter=new BotBlocksAdapter(botBlocks);
        recyclerView.setAdapter(botBlocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getApplicationContext()
                ,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setupBotBlocksDialog(BotBlocksResponse botBlocksResponse){
        botBlocksDialog=LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.bot_blocks_dialog,null);
        mWindowManager.addView(botBlocksDialog, getViewParams(100,200,600,600));
        showBotBlocks(botBlocksResponse.getBlocks().getDefault()
                ,botBlocksDialog.findViewById(R.id.default_blocks_recycler));
        showBotBlocks(botBlocksResponse.getBlocks().getNormal()
                ,botBlocksDialog.findViewById(R.id.normal_blocks_recycler));
        botBlocksDialog.findViewById(R.id.root).setOnTouchListener(dialogTouchListener);

        botBlocksDialog.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mWindowManager.updateViewLayout(botBlocksDialog,getViewParams(0,0,0,0));
                        mWindowManager.removeView(botBlocksDialog);
                    }
                });
    }

    private WindowManager.LayoutParams getViewParams(int x, int y, int width, int height){

        WindowManager.LayoutParams params;
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
        params.x = x;
        params.y = y;

        if (width!=-1)
            params.width=width;
        if (height!=-1)
            params.height=height;


        return params;
    }


    // method for making flasher to indicate getting user id from user name
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
