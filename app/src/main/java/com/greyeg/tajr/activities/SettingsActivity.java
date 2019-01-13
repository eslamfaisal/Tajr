package com.greyeg.tajr.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.greyeg.tajr.MainActivity;
import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.helper.font.RobotoTextView;
import com.rafakob.drawme.DrawMeRelativeLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {
    private String carrierName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @BindView(R.id.sim_view)
    DrawMeRelativeLayout simView;

    @BindView(R.id.sim_num)
    TextView simNum;

    @BindView(R.id.lang_view)
    DrawMeRelativeLayout langView;

    @BindView(R.id.lang_name)
    TextView langName;

    private String TAG = "ttttttttttt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        if (SharedHelper.getKey(getApplicationContext(), "sub_num").equals("1")) {
            simNum.setText(getString(R.string.default_sim_activ));
            simNum.setClickable(false);
        } else {
            if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("1")) {
                simNum.setText(getString(R.string.sim_1)+"  "+SharedHelper.getKey(getApplicationContext(), "activated_sub_net_name"));
            } else if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("2")) {
                simNum.setText(getString(R.string.sim_2)+"  "+SharedHelper.getKey(getApplicationContext(), "activated_sub_net_name"));
            }
        }
        if (SharedHelper.getKey(getApplicationContext(), "lang").equals("en")) {

            langName.setText(getString(R.string.english));

        } else if (SharedHelper.getKey(getApplicationContext(), "lang").equals("ar")) {

            langName.setText(getString(R.string.arabic));

        }

    }
//

    RadioGroup radioGroup;
    RobotoTextView ok;
    View view;
    String newStatus;
    String newStatusTag;
    RadioButton sim1;
    RadioButton sim2;
    Dialog dialog;

    @OnClick(R.id.sim_view)
    public void shooseSimNumber() {

        if (SharedHelper.getKey(getApplicationContext(), "sub_num").equals("1")) {
            return;
        }
        dialog = new Dialog(this);
        //  view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        dialog.setContentView(R.layout.dialog_choose_sim);
        radioGroup = dialog.findViewById(R.id.radio_group);
        sim1 = dialog.findViewById(R.id.sim1);
        sim2 = dialog.findViewById(R.id.sim2);
        if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("1")) {
            sim1.setChecked(true);
        } else if (SharedHelper.getKey(getApplicationContext(), "activated_sub_num").equals("2")) {
            sim2.setChecked(true);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList().size()==0||SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList().size()==1){
                simNum.setText(getString(R.string.default_sim_activ));
                return;
            }
            if (SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName() != null){
                sim1.setText(getString(R.string.sim_1)+"  "+SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName());

            }
            if (SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName() != null){
                sim2.setText(getString(R.string.sim_2)+"  "+SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName());

            }

        }
        ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton selectedRadioButton = (RadioButton) dialog.findViewById(radioGroup.getCheckedRadioButtonId());
                newStatus = selectedRadioButton.getText().toString();
                newStatusTag = (String) selectedRadioButton.getTag();

                if (newStatusTag.equals("1")) {
                    simNum.setText(getString(R.string.sim_1)+"  "+SharedHelper.getKey(getApplicationContext(), "activated_sub_net_name"));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String subIdForSim1 = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subIdForSim1 = String.valueOf(SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(0).getIccId());
                        Log.d(TAG, "activated_sub_id: = " + subIdForSim1);
                        SharedHelper.putKey(getApplicationContext(), "activated_sub_num", "1");
                        String netName = String.valueOf(SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName());
                        SharedHelper.putKey(getApplicationContext(), "activated_sub_net_name", netName);
                    }

                } else {
                    simNum.setText(getString(R.string.sim_2)+"  "+SharedHelper.getKey(getApplicationContext(), "activated_sub_net_name"));
                    String subIdForSim2 = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        subIdForSim2 = String.valueOf(SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(1).getIccId());
                        String netName = String.valueOf(SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName());
                        SharedHelper.putKey(getApplicationContext(), "activated_sub_net_name", netName);
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


    RadioGroup radioGrouplang;
    RobotoTextView okLang;
    View viewLang;
    String newStatusLAng;
    String newStatusTagLang;
    RadioButton arabic;
    RadioButton english;
    Dialog dialogLang;

    @OnClick(R.id.lang_view)
    public void shooseLang() {
        dialogLang = new Dialog(this);
        //  view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        dialogLang.setContentView(R.layout.dialog_choose_lang);
        radioGrouplang = dialogLang.findViewById(R.id.radio_group);
        arabic = dialogLang.findViewById(R.id.arabic);
        english = dialogLang.findViewById(R.id.english);
        if (SharedHelper.getKey(getApplicationContext(), "lang").equals("ar")) {
            arabic.setChecked(true);
        } else if (SharedHelper.getKey(getApplicationContext(), "lang").equals("en")) {
            english.setChecked(true);
        }
        okLang = dialogLang.findViewById(R.id.ok);
        okLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton selectedRadioButton = (RadioButton) dialogLang.findViewById(radioGrouplang.getCheckedRadioButtonId());
                newStatusLAng = selectedRadioButton.getText().toString();
                newStatusTagLang = (String) selectedRadioButton.getTag();
                dialogLang.dismiss();
                if (newStatusTagLang.equals("ar")) {
                    SharedHelper.putKey(getApplicationContext(), "lang", "ar");
                    setLocale("ar");


                } else {
                    SharedHelper.putKey(getApplicationContext(), "lang", "en");
                    setLocale("en");
                }


            }
        });

        dialogLang.show();
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        MainActivity.mainActivity.finish();
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();

    }

}
