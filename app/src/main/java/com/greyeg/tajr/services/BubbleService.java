package com.greyeg.tajr.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.adapters.BotBlocksAdapter;
import com.greyeg.tajr.adapters.ProductSpinnerAdapter;
import com.greyeg.tajr.adapters.SubscribersAdapter;
import com.greyeg.tajr.helper.ScreenHelper;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.UserNameEvent;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.BotBlock;
import com.greyeg.tajr.models.BotBlocksResponse;
import com.greyeg.tajr.models.Broadcast;
import com.greyeg.tajr.models.Cities;
import com.greyeg.tajr.models.NewOrderResponse;
import com.greyeg.tajr.models.ProductData;
import com.greyeg.tajr.models.ProductForSpinner;
import com.greyeg.tajr.models.Subscriber;
import com.greyeg.tajr.models.SubscriberInfo;
import com.greyeg.tajr.server.BaseClient;
import com.rafakob.drawme.DrawMeButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BubbleService extends Service
        implements BotBlocksAdapter.OnBlockSelected
            , SubscribersAdapter.OnSubscriberSelected {

    private WindowManager mWindowManager;
    private View bubbleView;
    private View deleteView;
    View expandedView;
    View botBlocksDialog;
    View subscribersDialog;
    View newOrderDialog;
    WindowManager.LayoutParams params;
    WindowManager.LayoutParams dialogParams;
    WindowManager.LayoutParams subscribersDialogParams;
    WindowManager.LayoutParams newOrderDialogParams;
    public  String  userName=null;
    public  String psid =null;
    public  String page=null;
    private String userId;
    public  String blockId=null;
    private static final int CLICK_ACTION_THRESHOLD = 0;
    private float startX;
    private float startY;
    public static boolean isRunning=false;
    private   boolean deleteViewAdded=false;
    private Handler handler;
    private int width,height;
    String productId;
    List<String> cities = new ArrayList<>();
    public static String CITY_ID;
    List<String> citiesId = new ArrayList<>();
    private List<Cities.City> citiesBody;
    ArrayList<Subscriber> subscribers=new ArrayList<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning=true;
        EventBus.getDefault().register(this);

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
                        Toast.makeText(BubbleService.this, "sending broadcast", Toast.LENGTH_SHORT).show();
                    }
                });

        bubbleView.findViewById(R.id.register_order)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setupNewOrderDialog();
                        bubbleView.setVisibility(View.GONE);

                    }
                });



    }



    @Subscribe()
    public void onMessageEvent(UserNameEvent event) {
        if (event.getUserNmae()!=null){
            Log.d("EVENTTT", event.getUserNmae()+" -------> : "+userName);
            if (userName == null || !userName.equals(event.getUserNmae())){
                psid =null;
                getUserId("Ahmed Khaled");
                Log.d("EVENTTT","getting userName");

            }

            userName=event.getUserNmae();
            Log.d("EVENTTT","set userName "+userName);

        }
        if (bubbleView==null)
            return;

        if (event.getUserNmae()!=null)
            bubbleView.findViewById(R.id.gotUserName)
            .setBackgroundResource(R.drawable.circle_green);
        else
            bubbleView.findViewById(R.id.gotUserName)
                    .setBackgroundResource(R.drawable.circle_gray);

    };


    private void getUserId(String userName){
        subscribers.clear();
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
                                psid =subscribersData.get(0).getPsid();
                                page=subscribersData.get(0).getPage();
                                userId=subscribersData.get(0).getId();

                            }else {
                                subscribers=subscribersData;
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
        setBroadCastLoading(View.VISIBLE);
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
                        setBroadCastLoading(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<BotBlocksResponse> call, Throwable t) {
                        Log.d("BOTBLOKSS", "onFailure: "+t.getMessage());
                        Toast.makeText(BubbleService.this,
                                "failed to get Blocks \n "+t.getMessage()
                                , Toast.LENGTH_SHORT).show();
                        setBroadCastLoading(View.GONE);

                    }
                });
    }



    private void pasteData(ImageView pasteButton,EditText editText){
        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData =clipboard.getPrimaryClip();
                if (clipData!=null&&clipData.getItemCount()>0)
                    editText.setText(clipboard.getPrimaryClip().getItemAt(0).getText());
                else
                    Toast.makeText(BubbleService.this, "no text copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void MakeNewOrder(String client_name, String client_order_phone1
            , String client_area, String client_address, String item_no){

        String[] params={client_name,client_order_phone1,client_area,client_address,item_no,CITY_ID,productId};
        for (String param : params) {
            if (TextUtils.isEmpty(param)) {
                Toast.makeText(this, "complete all Fields", Toast.LENGTH_SHORT).show();
                return;
            }

        }


        Log.d("ORDERRRR", "client_name: "+client_name);
        Log.d("ORDERRRR", "client_order_phone1: "+client_order_phone1);
        Log.d("ORDERRRR", "client_area: "+client_area);
        Log.d("ORDERRRR", "client_address: "+client_address);
        Log.d("ORDERRRR", "item_no: "+item_no);
        Log.d("ORDERRRR", "CITY ID: "+CITY_ID);
        Log.d("ORDERRRR", "product id: "+productId);



        BaseClient.getService()
                .recordNewOrder(SharedHelper.getKey(getApplicationContext(),LoginActivity.TOKEN),
                        userId,productId,client_name,client_order_phone1,CITY_ID,client_area,client_address
                ,item_no,null)
                .enqueue(new Callback<NewOrderResponse>() {
                    @Override
                    public void onResponse(Call<NewOrderResponse> call, Response<NewOrderResponse> response) {
                        NewOrderResponse newOrderResponse=response.body();
                        if (newOrderResponse!=null)
                        Log.d("ORDERRRR", "onresponse: "+newOrderResponse.getData());
                        else
                        Log.d("ORDERRRR", "onresponse: "+response.code());

                    }

                    @Override
                    public void onFailure(Call<NewOrderResponse> call, Throwable t) {
                        Log.d("ORDERRRR", "onFailure: "+t.getMessage());
                    }
                });
    }


    private void setupSubscribersDialog(ArrayList<Subscriber> subscribers){
        subscribersDialog=LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.subscripers_dialog,null);
        subscribersDialogParams=getViewParams(100,200,600,600,subscribersDialogParams);
        mWindowManager.addView(subscribersDialog,subscribersDialogParams);

        RecyclerView recyclerView=subscribersDialog.findViewById(R.id.subscribers_recycler);
        SubscribersAdapter adapter=new SubscribersAdapter(
                getApplicationContext(),subscribers,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));




        subscribersDialog.findViewById(R.id.delete)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mWindowManager.removeView(subscribersDialog);
                    }
                });

        subscribersDialog.setOnTouchListener(subscribersdialogTouchListener);

    }

    private void setupBotBlocksDialog(BotBlocksResponse botBlocksResponse){
        botBlocksDialog=LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.bot_blocks_dialog,null);
        dialogParams=getViewParams(100,200,600,600,dialogParams);
        mWindowManager.addView(botBlocksDialog,dialogParams);
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

    private void showBotBlocks(ArrayList<BotBlock> botBlocks, RecyclerView recyclerView){
        BotBlocksAdapter botBlocksAdapter=new BotBlocksAdapter(botBlocks,this);
        recyclerView.setAdapter(botBlocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getApplicationContext()
                ,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setupNewOrderDialog(){

        newOrderDialog=LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.new_order_dialog,null);
        newOrderDialogParams=getViewParams(100,100,600,600,newOrderDialogParams);
        mWindowManager.addView(newOrderDialog,newOrderDialogParams);


        newOrderDialog.findViewById(R.id.close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mWindowManager.removeView(newOrderDialog);
                        bubbleView.setVisibility(View.VISIBLE);
                    }
                });
        newOrderDialog.findViewById(R.id.minimize)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View orderView=newOrderDialog.findViewById(R.id.order_view);
                        if (orderView.getVisibility()==View.VISIBLE){
                            orderView.setVisibility(View.GONE);
                            newOrderDialogParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
                            newOrderDialogParams.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                        }
                        else{
                            orderView.setVisibility(View.VISIBLE);
                            newOrderDialogParams.height=600;
                            newOrderDialogParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

                        }


                        newOrderDialog.setLayoutParams(newOrderDialogParams);
                        mWindowManager.updateViewLayout(newOrderDialog,newOrderDialogParams);

                    }
                });

        getProducts();
        getCities();

        Spinner product=newOrderDialog.findViewById(R.id.product);
        Spinner city=newOrderDialog.findViewById(R.id.client_city);
        EditText client_name=newOrderDialog.findViewById(R.id.client_name);
        EditText client_address=newOrderDialog.findViewById(R.id.client_address);
        EditText client_area=newOrderDialog.findViewById(R.id.client_area);
        EditText client_order_phone1=newOrderDialog.findViewById(R.id.client_order_phone1);
        EditText item_no=newOrderDialog.findViewById(R.id.item_no);
        DrawMeButton send_order=newOrderDialog.findViewById(R.id.send_order);

        ImageView client_name_Paste=newOrderDialog.findViewById(R.id.client_name_paste);
        ImageView client_address_paste=newOrderDialog.findViewById(R.id.client_address_paste);
        ImageView client_area_paste=newOrderDialog.findViewById(R.id.client_area_paste);
        ImageView client_order_phone1_paste=newOrderDialog.findViewById(R.id.client_order_phone1_paste);
        ImageView item_no_paste=newOrderDialog.findViewById(R.id.item_no_paste);


        newOrderDialog.setOnTouchListener(setOnTOuchListener(newOrderDialogParams));

        send_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeNewOrder(
                        client_name.getText().toString(),
                        client_order_phone1.getText().toString(),
                        client_area.getText().toString(),
                        client_address.getText().toString(),
                        item_no.getText().toString()

                );
            }
        });





        pasteData(client_name_Paste,client_name);
        pasteData(client_address_paste,client_address);
        pasteData(client_area_paste,client_area);
        pasteData(client_order_phone1_paste,client_order_phone1);
        pasteData(item_no_paste,item_no);


    }



    @Override
    public void onBlockSelected(String blockId) {
        setBroadCastLoading(View.VISIBLE);
        mWindowManager.removeView(botBlocksDialog);
        String token=SharedHelper.getKey(getApplicationContext(),LoginActivity.TOKEN);
        BaseClient.getService()
                .sendBroadcast(token, psid,page,blockId)
                .enqueue(new Callback<Broadcast>() {
                    @Override
                    public void onResponse(Call<Broadcast> call, Response<Broadcast> response) {
                        Broadcast broadcast=response.body();
                        if (broadcast!=null){
                            Toast.makeText(BubbleService.this, broadcast.getData(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BubbleService.this, "Error sending Broadcast ", Toast.LENGTH_SHORT).show();
                        }
                        setBroadCastLoading(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<Broadcast> call, Throwable t) {
                        Log.d("BROADCASTTT", "onFailure: "+t.getMessage());
                        Toast.makeText(BubbleService.this, "Error sending Broadcast", Toast.LENGTH_SHORT).show();
                        setBroadCastLoading(View.GONE);

                    }
                });
    }

    @Override
    public void onSubscriberSelected(String psid,String userId) {
        this.psid =psid;
        this.userId=userId;
        mWindowManager.removeView(subscribersDialog);
        bubbleView.findViewById(R.id.expanded_bubble).setVisibility(View.VISIBLE);
    }

    private void stopFlasher(){
        handler.removeCallbacksAndMessages(null);
        //handler.removeCallbacks(runnable);
        bubbleView.findViewById(R.id.gotUserId)
                .setBackgroundResource(R.drawable.circle_green);
    }

    private void setBroadCastLoading(int visibility){
        bubbleView.findViewById(R.id.broadcast_progressBar)
                .setVisibility(visibility);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bubbleView != null) mWindowManager.removeView(bubbleView);
        if (newOrderDialog != null) mWindowManager.removeView(newOrderDialog);
        if (subscribersDialog != null) mWindowManager.removeView(subscribersDialog);
        if (botBlocksDialog != null) mWindowManager.removeView(botBlocksDialog);
        isRunning=false;
        EventBus.getDefault().unregister(this);
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

                handler.postDelayed(this,150);
                run=!run;
            }
        });

//        bubbleView.findViewById(R.id.gotUserId)
//                .setBackgroundResource(R.drawable.circle_green);
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }

    private WindowManager.LayoutParams getViewParams(int x, int y, int width, int height,WindowManager.LayoutParams layoutParams){


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        }else{
            layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
        }

        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = x;
        layoutParams.y = y;

        if (width!=-1)
            layoutParams.width=width;
        if (height!=-1)
            layoutParams.height=height;



        return layoutParams;
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
                        if (userName==null&& psid ==null){
                            bubbleView.findViewById(R.id.gotUserName)
                                    .setBackgroundResource(R.drawable.circle_gray);
                            bubbleView.findViewById(R.id.gotUserId)
                                    .setBackgroundResource(R.drawable.circle_gray);
                            expandedView.setVisibility(View.GONE);
                            return true;
                        }
                        if (userName!=null){
                            bubbleView.findViewById(R.id.gotUserName)
                                    .setBackgroundResource(R.drawable.circle_green);
                        }
                        if (subscribers!=null&&subscribers.size()>0&&psid==null){
                            setupSubscribersDialog(subscribers);
                            Log.d("SUBSCRIBERSS", " ");
                        }

                        if (psid !=null){
                            if (expandedView.getVisibility()==View.GONE)
                                expandedView.setVisibility(View.VISIBLE);
                            else
                                expandedView.setVisibility(View.GONE);
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
                                            ,-1,-1,dialogParams));
                            deleteViewAdded=true;
                        }
                    }else{
                        if (deleteViewAdded){
                            mWindowManager.removeView(deleteView);
                            deleteViewAdded=false;
                        }

                    }


                    if (event.getRawY()>height-100  //80
                            &&event.getRawX()>width/2-40  //20
                            &&event.getRawX()<width/2+100  //80
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

                    if (bubbleView!=null)
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
                    initialX = dialogParams.x;
                    initialY = dialogParams.y;


                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    dialogParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    dialogParams.y = initialY + (int) (event.getRawY() - initialTouchY);


                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(botBlocksDialog, dialogParams);
                    return true;
            }
            return false;
        }
    };

    View.OnTouchListener subscribersdialogTouchListener =new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            if (subscribersDialogParams==null){
                Log.d("subscribersDialogParams", "onTouch: null subscribersDialogParams");
                return true;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:


                    //remember the initial position.
                    initialX = subscribersDialogParams.x;
                    initialY = subscribersDialogParams.y;


                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    subscribersDialogParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    subscribersDialogParams.y = initialY + (int) (event.getRawY() - initialTouchY);


                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(subscribersDialog, subscribersDialogParams);
                    return true;
            }
            return false;
        }
    };

    private View.OnTouchListener setOnTOuchListener(WindowManager.LayoutParams viewparams){
        View.OnTouchListener viewTouchListener =new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (viewparams==null){
                    Log.d("subscribersDialogParams", "onTouch: null subscribersDialogParams");
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        //remember the initial position.
                        initialX = viewparams.x;
                        initialY = viewparams.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        viewparams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        viewparams.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        if (newOrderDialog!=null)
                        mWindowManager.updateViewLayout(newOrderDialog, viewparams);
                        return true;
                }
                return false;
            }
        };

        return viewTouchListener;

    }


    private void getProducts() {
        Spinner product=newOrderDialog.findViewById(R.id.product);

        BaseClient.getService().getProducts(SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN), null)
                .enqueue(new Callback<AllProducts>() {
            @Override
            public void onResponse(Call<AllProducts> call, final Response<AllProducts> response) {
                Log.d("eeeeeeeeeeeeeee", "response: " + response.body().getProducts_count());
                AllProducts allProducts=response.body();
                if (allProducts!= null&&allProducts.getProducts()!=null) {
                    if (allProducts.getProducts().size() > 0) {
                        productId = allProducts.getProducts().get(0).getProduct_id();

                    }
                    List<ProductForSpinner> products = new ArrayList<>();
                    for (ProductData product : response.body().getProducts()) {
                        products.add(new ProductForSpinner(product.getProduct_name(), product.getProduct_image(), product.getProduct_id(),product.getProduct_real_price()));
                    }
                    ArrayAdapter<String> myAdapter = new ProductSpinnerAdapter(
                            getApplicationContext(), products,R.layout.layout_product_spinner_item2);
                    product.setAdapter(myAdapter);

                    product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            productId = allProducts.getProducts().get(position).getProduct_id();
                            //Log.d("ORDERRRR", "product : "+productId);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AllProducts> call, Throwable t) {
                Log.d("eeeeeeeeeeeeeee", "onFailure: " + t.getMessage());
            }
        });

    }

    private void getCities() {
        Spinner client_city=newOrderDialog.findViewById(R.id.client_city);
        if (cities != null) {
            cities.clear();
            Log.d("ORDERRRR", "clear Cities: ");
        }
        Call<Cities> getCitiesCall = BaseClient.getService().getCities(
                SharedHelper.getKey(getApplicationContext(), LoginActivity.TOKEN),
                SharedHelper.getKey(getApplicationContext(), LoginActivity.PARENT_ID)
        );

        getCitiesCall.enqueue(new Callback<Cities>() {
            @Override
            public void onResponse(Call<Cities> call, Response<Cities> response) {
                if (response.body().getCities() != null) {
                    if (response.body().getCities().size() > 0) {

                        citiesBody = response.body().getCities();
                        for (Cities.City city : citiesBody) {
                            cities.add(city.getCity_name()+" >> "+
                                    NumberFormat.getNumberInstance(Locale.getDefault()).format(
                                            Integer.valueOf(city.getShipping_cost()))+
                                    getString(R.string.le)+" "
                                    +getString(R.string.for_shipping));
                            citiesId.add(city.getCity_id());
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext()
                                , R.layout.layout_cities_spinner_item, cities);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        client_city.setAdapter(adapter);
                        client_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                CITY_ID = String.valueOf(citiesBody.get(position).getCity_id());
                                //Log.d("ORDERRRR", "onItemSelected: "+CITY_ID);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<Cities> call, Throwable t) {

            }
        });
    }




}
