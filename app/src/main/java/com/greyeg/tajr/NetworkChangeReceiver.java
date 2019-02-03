package com.greyeg.tajr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.greyeg.tajr.helper.NetworkUtil;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("mmmmmmmmmmm", "onReceive: fffffffffff");
        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("Sulodetwork reciever", "Sulod sa network reciever");
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                Toast.makeText(context, "lhad", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "lhad", Toast.LENGTH_SHORT).show();
                Log.d("mmmmmmmmmmm", "onReceive: fffffffffff");
            }
        }
    }
}