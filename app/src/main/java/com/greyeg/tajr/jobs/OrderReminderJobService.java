package com.greyeg.tajr.jobs;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.activities.OrderActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.SimpleOrderResponse;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by aviator on 16/03/18.
 */

public class OrderReminderJobService extends JobService {

    private static AsyncTask mBackgroundTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        Api api = BaseClient.getBaseClient().create(Api.class);
        api.getFuckenOrders(SharedHelper.getKey(this, LoginActivity.TOKEN)).enqueue(new Callback<SimpleOrderResponse>() {
            @Override
            public void onResponse(Call<SimpleOrderResponse> call, Response<SimpleOrderResponse> response) {
                if (response.body() != null) {
                    if (response.body().getOrder()!=null){
                        createNotification(String.valueOf(response.body().getRemainig_orders()));
                        jobFinished(jobParameters, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleOrderResponse> call, Throwable t) {
                createNotification(String.valueOf(t.getMessage()));
            }
        });



        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null)
            mBackgroundTask.cancel(true);
        return true;
    }


    @SuppressLint("NewApi")
    public void createNotification(String first) {

        Intent intent2 = new Intent(getApplicationContext(), OrderActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, 0);

//        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_launcher);
//
//        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.notification_icon, getResources().getString(R.string.cancel), pendingIntent2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("5", "eslam", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("5");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "5")
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle("orders")
                .setOngoing(true)
                .setColor(Color.RED)
                 .addAction(action)
                .setContentText(getString(R.string.remaining) + " " + first + " " + getString(R.string.order))
                .setSmallIcon(R.drawable.ic_launcher);

        Intent intent = new Intent(this, OrderActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        //builder.setContentIntent(pendingIntent);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(5, builder.build());
        Toast.makeText(this, "created", Toast.LENGTH_SHORT).show();


    }
}