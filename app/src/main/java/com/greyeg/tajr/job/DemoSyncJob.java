package com.greyeg.tajr.job;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greyeg.tajr.activities.ChatActivity;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.Message;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DemoSyncJob extends Job {

    public static final String TAG = "job_demo_tag";

    @Override
    @NonNull
    protected Result onRunJob(@NonNull final Params params) {

        boolean success = new DemoSyncEngine(getContext()).sync();
        final PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), ChatActivity.class), 0);


//        if (!run) {
            run = true;
        if (isNetworkAvailable())
           // get(pendingIntent);
//        }

        JobManager.instance().cancel(Integer.parseInt(SharedHelper.getKey(getContext(), "job_key")));
        Log.d("kkkkkkkkkkkkk", "onDataChange: " + SharedHelper.getKey(getContext(), "job_key"));
           // scheduleAdvancedJob();


        return success ? Result.SUCCESS : Result.FAILURE;
    }

    boolean run = false;

    synchronized private void get(final PendingIntent pendingIntent) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("notification");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final Message noty = dataSnapshot.getValue(Message.class);
                    if(!noty.getUserId().equals(SharedHelper.getKey(getContext(), LoginActivity.USER_ID))){
                        if (!dataSnapshot.child("seen").hasChild(SharedHelper.getKey(getContext(), LoginActivity.USER_ID))) {
                            FirebaseDatabase.getInstance().getReference().child("notification").child("seen")
                                    .child(SharedHelper.getKey(getContext(), LoginActivity.USER_ID))
                                    .setValue(SharedHelper.getKey(getContext(), LoginActivity.USER_ID)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        NotificationChannel channel = new NotificationChannel(TAG, "Job Demo", NotificationManager.IMPORTANCE_LOW);
                                        channel.setDescription("Job demo job");
                                        getContext().getSystemService(NotificationManager.class).createNotificationChannel(channel);
                                    }
                                    Notification notification = new NotificationCompat.Builder(getContext(), TAG)
                                            .setContentTitle(noty.getUserName())
                                            .setContentText(noty.getMessage())
                                            .setAutoCancel(true)
                                            .setChannelId(TAG)
                                            .setSound(null)
                                            .setContentIntent(pendingIntent)
                                            .setSmallIcon(android.R.drawable.ic_menu_slideshow)
                                            .setColor(Color.GREEN)
                                            .build();
                                    NotificationManagerCompat.from(getContext()).notify(new Random().nextInt(), notification);
                                    run = false;
                                }
                            });

                        } else {
                            run = false;

                        }
                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void scheduleAdvancedJob() {


        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString("key", "Hello world");

        int jobId = new JobRequest.Builder(DemoSyncJob.TAG)
                .setExecutionWindow(1_000L, 4_000L)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();

        SharedHelper.putKey(getContext(), "job_key", String.valueOf(jobId));
    }

    public static void scheduleJob() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }
//
//    private void scheduleAdvancedJob() {
//        PersistableBundleCompat extras = new PersistableBundleCompat();
//        extras.putString("key", "Hello world");
//
//        int jobId = new JobRequest.Builder(DemoSyncJob.TAG)
//                .setExecutionWindow(30_000L, 40_000L)
//                .setBackoffCriteria(5_000L, JobRequest.BackoffPolicy.EXPONENTIAL)
//                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(false)
//                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
//                .setExtras(extras)
//                .setRequirementsEnforced(true)
//                .setUpdateCurrent(true)
//                .build()
//                .schedule();
//    }

    private void schedulePeriodicJob() {
        int jobId = new JobRequest.Builder(DemoSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .build()
                .schedule();
    }

    private void scheduleExactJob() {
        int jobId = new JobRequest.Builder(DemoSyncJob.TAG)
                .setExact(20_000L)
                .build()
                .schedule();
    }

    private void runJobImmediately() {
        int jobId = new JobRequest.Builder(DemoSyncJob.TAG)
                .startNow()
                .build()
                .schedule();
    }

    private void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
    }

}