package com.greyeg.tajr.call_receiver;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.greyeg.tajr.models.OutgoingCallData;
import com.greyeg.tajr.order.CurrentOrderData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService  {
    public static Call callme;

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Call.Callback callback = new Call.Callback() {
            @Override
            public void onStateChanged(Call call, int state) {
                super.onStateChanged(call, state);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Toast.makeText(this, "" + call.toString(), Toast.LENGTH_SHORT).show();
        callme = call;
        call.registerCallback(new Call.Callback() {
            @Override
            public void onStateChanged(Call call, int state) {
                super.onStateChanged(call, state);
                Call.Details details =  call.getDetails();
                if (details.getHandle().getSchemeSpecificPart().equals(CurrentOrderData.getInstance().getCurrentOrderResponse().getOrder().getPhone1())&&state==Call.STATE_DIALING){
                    OutgoingCallData outgoingCallData = new OutgoingCallData();
                    outgoingCallData.setNumber(details.getHandle().getSchemeSpecificPart());
                    outgoingCallData.setState(state);
                    Intent sendLevel = new Intent();
                    sendLevel.setAction("CallService");
                    sendLevel.putExtra( "outgoingCallData",outgoingCallData);
                    sendBroadcast(sendLevel);
                }else {
                    if (state==Call.STATE_RINGING){
                        Log.d("eslamfaisal", "onStateChanged: "+"STATE_RINGING");
                    }else {
                        Log.d("eslamfaisal", "onStateChanged: "+state);

                        Log.d("eslamfaisal", "onDetailsChanged: "+ details.getHandle().getSchemeSpecificPart()+"\n"+details.getConnectTimeMillis());
                        details.getHandle().getSchemeSpecificPart();
                        call.hold();
                    }
                }


            }
        });


    }


}
