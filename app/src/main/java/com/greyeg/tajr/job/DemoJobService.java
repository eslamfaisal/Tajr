package com.greyeg.tajr.job;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.Message;

import java.util.Random;

/**
 * A very simple JobService that merely stores its result and immediately finishes.
 */
public class DemoJobService extends JobService {
    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        Toast.makeText(this, "يارب", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(DemoJobService.this, "يارب يارب", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return false; // No more work to do
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {

        return false; // No more work to do
    }

    void newjob(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString("some_key", "some_value");

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(DemoJobService.class)
                // uniquely identifies the job
                .setTag("my-unique-tag")
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(0, 2))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
//                        ,
//                        // only run when the device is charging
//                        Constraint.DEVICE_CHARGING
                )
                .setExtras(myExtrasBundle)
                .build();

        dispatcher.mustSchedule(myJob);
    }

    synchronized private void get() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("notification");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final Message noty = dataSnapshot.getValue(Message.class);
                    if (!noty.getUserId().equals("112")) {
                        if (!dataSnapshot.child("seen").hasChild("112")) {
                            FirebaseDatabase.getInstance().getReference().child("notification").child("seen")
                                    .child("111")
                                    .setValue("111").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

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

                                }
                            });

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

}