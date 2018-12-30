package com.greyeg.tajr.call_receiver;

/**
 * Created by jeet on 24/12/16.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.util.Date;


public class CallReceiver extends PhoneCallReceiver {
    Context context;

    @Override
    protected void onIncomingCallStarted(final Context ctx, String pnumber, Date start) {
        Toast.makeText(ctx, "Jeet Incoming Call from " + pnumber, Toast.LENGTH_LONG).show();
        context = ctx;
        final Intent intent = new Intent(context, MyCustomDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("phone_no", pnumber);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        }, 2000);

//        MyCus/*tomDialog dialog   =   new MyCustomDialog(context);
//        dialog.*/show();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String pnumber, Date start, Date end) {
        Toast.makeText(ctx, "Call dropped" + pnumber, Toast.LENGTH_LONG).show();
    }
}