package com.greyeg.tajr.job;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greyeg.tajr.models.Message;

import java.util.Random;

public class JobServicio extends JobService {
    String TAG ="EPA";
    String LOG_TAG ="EPA";
    ConnectivityManager.NetworkCallback networkCallback;
    BroadcastReceiver connectivityChange;
    ConnectivityManager connectivityManager;
    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i(LOG_TAG, "Job created");
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Toast.makeText(this, "الجوب وقفت", Toast.LENGTH_SHORT).show();
        if(networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)connectivityManager.unregisterNetworkCallback(networkCallback);
        else if(connectivityChange != null)unregisterReceiver(connectivityChange);
        return true;
    }
    private void handleConnectivityChange(NetworkInfo networkInfo){
        // Calls handleConnectivityChange(boolean connected, int type)
    }
    private void handleConnectivityChange(boolean connected, int type){
        // Calls handleConnectivityChange(boolean connected, ConnectionType connectionType)
        Toast.makeText(this, "erga", Toast.LENGTH_SHORT).show();
    }
    private void handleConnectivityChange(boolean connected, ConnectionType connectionType){
        // Logic based on the new connection
    }
    private enum ConnectionType{
        MOBILE,WIFI,VPN,OTHER;
    }
    ConnectivityManager.NetworkCallback x = new ConnectivityManager.NetworkCallback() { //this networkcallback work wonderfull

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onAvailable(Network network) {
            Log.d(TAG, "requestNetwork onAvailable()");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //do something
            }
            else {
                //This method was deprecated in API level 23
                ConnectivityManager.setProcessDefaultNetwork(network);

            }
        }
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            Log.d(TAG, ""+network+"|"+networkCapabilities);
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            Log.d(TAG, "requestNetwork onLinkPropertiesChanged()");
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            Log.d(TAG, "requestNetwork onLosing()");
        }

        @Override
        public void onLost(Network network) {
        }
    };
    private void get() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("notification");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final Message noty = dataSnapshot.getValue(Message.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("فشل", "Job Demo", NotificationManager.IMPORTANCE_LOW);
                        channel.setDescription("Job demo job");
                        getApplicationContext().getSystemService(NotificationManager.class).createNotificationChannel(channel);
                    }
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), "فشل")
                            .setContentTitle(noty.getUserName())
                            .setContentText(noty.getMessage())
                            .setAutoCancel(true)
                            .setChannelId("فشل")
                            .setSound(null)
                            // .setContentIntent(pendingIntent)
                            .setSmallIcon(android.R.drawable.ic_menu_slideshow)
                            .setColor(Color.GREEN)
                            .build();
                    NotificationManagerCompat.from(getApplicationContext()).notify(new Random().nextInt(), notification);

//                    if (!noty.getUserId().equals("112")) {
//                        if (!dataSnapshot.child("seen").hasChild("112")) {
//                            FirebaseDatabase.getInstance().getReference().child("notification").child("seen")
//                                    .child("111")
//                                    .setValue("111").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                }
//                            });
//
//                        }
//                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }

    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                onConnectionFound();
            } else {
                onConnectionLost();
            }
        }
    };


    public void onConnectionLost() {
        Log.d("adadad", "onConnectionLost: dis");
       // FirebaseDatabase.getInstance().goOffline();
       // noInter.setVisibility(View.VISIBLE);
    }

    public void onConnectionFound() {
        FirebaseDatabase.getInstance().goOnline();
        get();
        Log.d("adadad", "onConnectionLost: co");
      //  noInter.setVisibility(View.GONE);

    }




}