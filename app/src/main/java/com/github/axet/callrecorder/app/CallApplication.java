package com.github.axet.callrecorder.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;

import androidx.multidex.MultiDex;
import androidx.preference.PreferenceManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import android.util.DisplayMetrics;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.axet.androidlibrary.app.NotificationManagerCompat;
import com.github.axet.androidlibrary.widgets.NotificationChannelCompat;
import com.github.axet.androidlibrary.widgets.OptimizationPreferenceCompat;
import com.greyeg.tajr.App;
import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.ChatActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;


import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Locale;

public class CallApplication extends com.github.axet.audiolibrary.app.MainApplication {
    public static final String PREFERENCE_DELETE = "delete";
    public static final String PREFERENCE_FORMAT = "format";
    public static final String PREFERENCE_CALL = "call";
    public static final String PREFERENCE_OPTIMIZATION = "optimization";
    public static final String PREFERENCE_NEXT = "next";
    public static final String PREFERENCE_DETAILS_CONTACT = "_contact";
    public static final String PREFERENCE_DETAILS_CALL = "_call";
    public static final String PREFERENCE_SOURCE = "source";
    public static final String PREFERENCE_FILTER_IN = "filter_in";
    public static final String PREFERENCE_FILTER_OUT = "filter_out";
    public static final String PREFERENCE_DONE_NOTIFICATION = "done_notification";
    public static final String PREFERENCE_MIXERPATHS = "mixer_paths";
    public static final String PREFERENCE_VOICE = "voice";
    public static final String PREFERENCE_VOLUME = "volume";
    public static final String PREFERENCE_VERSION = "version";
    public static final String PREFERENCE_BOOT = "boot";
    public static final String PREFERENCE_INSTALL = "install";

    public static final String CALL_OUT = "out";
    public static final String CALL_IN = "in";

    public NotificationChannelCompat channelPersistent;
    public NotificationChannelCompat channelStatus;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @SuppressWarnings("unchecked")
    @SuppressLint("PrivateApi")
    public static String getprop(String key) {
        try {
            Class klass = Class.forName("android.os.SystemProperties");
            Method method = klass.getMethod("get", String.class);
            return (String) method.invoke(null, key);
        } catch (Exception e) {
            Log.d(TAG, "no system prop", e);
            return null;
        }
    }

    public static CallApplication from(Context context) {
        return (CallApplication) com.github.axet.audiolibrary.app.MainApplication.from(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        channelPersistent = new NotificationChannelCompat(this, "icon", "Persistent Icon", NotificationManagerCompat.IMPORTANCE_LOW);
        channelStatus = new NotificationChannelCompat(this, "status", "Status", NotificationManagerCompat.IMPORTANCE_LOW);

        OptimizationPreferenceCompat.setPersistentServiceIcon(this, true);

        switch (getVersion(PREFERENCE_VERSION, R.xml.pref_general)) {
            case -1:
                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor e = shared.edit();
                MixerPaths m = new MixerPaths();
                if (!m.isSupported() || !m.isEnabled()) {
                    e.putString(CallApplication.PREFERENCE_ENCODING, Storage.EXT_3GP);
                }
                SharedPreferences.Editor edit = shared.edit();
                edit.putInt(PREFERENCE_VERSION, 2);
                edit.commit();
                break;
            case 0:
                version_0_to_1();
                break;
            case 1:
                version_1_to_2();
                break;
        }
        // OneSignal Initialization
        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

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
    private Locale locale = null;
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


    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            String rawPayload = notification.payload.rawPayload;

            String customKey;

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);
            Log.i("OneSignalExample", "NotificationID received: " + notificationID);

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl
            String customKey2 = data.optString("Eslam");
            Log.i("OneSignalExample", "customkey data " + customKey2);
            String customKey;
            String openURL = null;
            Object activityToLaunch = ChatActivity.class;

            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = ChatActivity.class;
                } else
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", openURL);
            Log.i("OneSignalExample", "openURL = " + openURL);
            // startActivity(intent);
            startActivity(intent);

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
        /*
           <application ...>
             <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
           </application>
        */
        }
    }



    void version_0_to_1() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = shared.edit();
        edit.putFloat(PREFERENCE_VOLUME, shared.getFloat(PREFERENCE_VOLUME, 0) + 1); // update volume from 0..1 to 0..1..4
        edit.putInt(PREFERENCE_VERSION, 1);
        edit.commit();
    }

    void version_1_to_2() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = shared.edit();
        edit.remove(PREFERENCE_SORT);
        edit.putInt(PREFERENCE_VERSION, 2);
        edit.commit();
    }

    public static String getContact(Context context, Uri f) {
        final SharedPreferences shared = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String p = getFilePref(f) + PREFERENCE_DETAILS_CONTACT;
        return shared.getString(p, null);
    }

    public static void setContact(Context context, Uri f, String id) {
        final SharedPreferences shared = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String p = getFilePref(f) + PREFERENCE_DETAILS_CONTACT;
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(p, id);
        editor.commit();
    }

    public static String getCall(Context context, Uri f) {
        final SharedPreferences shared = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String p = getFilePref(f) + PREFERENCE_DETAILS_CALL;
        return shared.getString(p, null);
    }

    public static void setCall(Context context, Uri f, String id) {
        final SharedPreferences shared = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String p = getFilePref(f) + PREFERENCE_DETAILS_CALL;
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(p, id);
        editor.commit();
    }

    public static String getString(Context context, Locale locale, int id, Object... formatArgs) {
        return getStringNewRes(context, locale, id, formatArgs);
    }

    public static String getStringNewRes(Context context, Locale locale, int id, Object... formatArgs) {
        Resources res;
        Configuration conf = new Configuration(context.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= 17)
            conf.setLocale(locale);
        else
            conf.locale = locale;
        res = new Resources(context.getAssets(), context.getResources().getDisplayMetrics(), conf);
        String str;
        if (formatArgs.length == 0)
            str = res.getString(id);
        else
            str = res.getString(id, formatArgs);
        new Resources(context.getAssets(), context.getResources().getDisplayMetrics(), context.getResources().getConfiguration()); // restore side effect
        return str;
    }

    public static String[] getStrings(Context context, Locale locale, int id) {
        Resources res;
        Configuration conf = new Configuration(context.getResources().getConfiguration());
        if (Build.VERSION.SDK_INT >= 17)
            conf.setLocale(locale);
        else
            conf.locale = locale;
        res = new Resources(context.getAssets(), context.getResources().getDisplayMetrics(), conf);
        String[] str;
        str = res.getStringArray(id);
        new Resources(context.getAssets(), context.getResources().getDisplayMetrics(), context.getResources().getConfiguration()); // restore side effect
        return str;
    }
}
