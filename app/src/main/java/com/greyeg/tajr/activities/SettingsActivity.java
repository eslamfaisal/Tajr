package com.greyeg.tajr.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.SharedHelper;
import com.rafakob.drawme.DrawMeRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.sim_view)
    DrawMeRelativeLayout simView;

    @BindView(R.id.sim_num)
    TextView simNum;
    private String TAG = "ttttttttttt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        if (SharedHelper.getKey(getApplicationContext(), "sub_num").equals("1")) {
            simNum.setText(getString(R.string.default_sim_activ));
            simNum.setClickable(false);
        }else {
            if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("1")) {
                simNum.setText(getString(R.string.sim_1));
            } else if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("2")){
                simNum.setText(getString(R.string.sim_2));
            }
        }
    }


    RadioGroup radioGroup;
    Button ok;
    View view;
    String newStatus;
    String newStatusTag;
    RadioButton sim1;
    RadioButton sim2;

    @OnClick(R.id.sim_view)
    public void shooseSimNumber() {
        if (SharedHelper.getKey(getApplicationContext(), "sub_num").equals("1")){
            return;
        }
        final Dialog dialog = new Dialog(this);
        view = LayoutInflater.from(this).inflate(R.layout.layout_order_status_list, null);
        dialog.setContentView(view);
        radioGroup = view.findViewById(R.id.radio_group);
        sim1 = radioGroup.findViewById(R.id.sim1);
        sim2 = radioGroup.findViewById(R.id.sim2);
        if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("1")) {
            sim1.setChecked(true);
        } else if(SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("2")) {
            sim2.setChecked(true);
        }
        ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton selectedRadioButton = (RadioButton) view.findViewById(radioGroup.getCheckedRadioButtonId());
                newStatus = selectedRadioButton.getText().toString();
                newStatusTag = (String) selectedRadioButton.getTag();

                if (newStatusTag.equals("1")) {
                    simNum.setText(getString(R.string.sim_1));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String subIdForSim1 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subIdForSim1 = String.valueOf(SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(0).getIccId());
                        Log.d(TAG, "activated_sub_id: = " + subIdForSim1);
                        SharedHelper.putKey(getApplicationContext(), "activated_sub_num", "1");
                    }

                } else {
                    simNum.setText(getString(R.string.sim_2));
                    String subIdForSim2 = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subIdForSim2 = String.valueOf(SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(1).getIccId());
                        SharedHelper.putKey(getApplicationContext(), "activated_sub_id", subIdForSim2);
                        Log.d(TAG, "activated_sub_id: " + subIdForSim2);
                        SharedHelper.putKey(getApplicationContext(), "activated_sub_num", "2");
                    }


                }

                dialog.dismiss();

            }
        });

        dialog.show();
    }


}
