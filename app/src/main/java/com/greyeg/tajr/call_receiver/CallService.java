package com.greyeg.tajr.call_receiver;

import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {
    public static Call callme;

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Toast.makeText(this, "" + call.getRemainingPostDialSequence(), Toast.LENGTH_SHORT).show();
        callme = call;
    }
}
