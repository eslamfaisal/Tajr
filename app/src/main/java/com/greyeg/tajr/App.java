package com.greyeg.tajr;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class App extends Application {

    private Locale locale = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
            Configuration config = new Configuration(newConfig);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("gemy/Changa-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Fresco.initialize(this);

        if (SharedHelper.getKey(getApplicationContext(), "lang").equals("en")) {

            setLocale("en");

        }else if (SharedHelper.getKey(getApplicationContext(), "lang").equals("ar")) {

            setLocale("ar");

        }
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(this, LoginActivity.class);
//        startActivity(refresh);
//        finish();
    }
    public void changeLang(String lang) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration conf = new Configuration(config);
        conf.locale = locale;
        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());

    }

//    public String getLang(){
//        return PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.locale_lang), "");
//    }


}
