package com.greyeg.tajr.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.greyeg.tajr.MainActivity;
import com.greyeg.tajr.R;
import com.greyeg.tajr.helper.SharedHelper;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharedHelper.getKey(getApplicationContext(), "lang").equals("en")) {

                    setLocale("en");

                }else if (SharedHelper.getKey(getApplicationContext(), "lang").equals("ar")) {

                    setLocale("ar");

                }else {
                    Intent refresh = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(refresh);
                    finish();
                }

            }
        },10);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LoginActivity.class);
        startActivity(refresh);
        finish();
    }


}
