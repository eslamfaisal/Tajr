package com.greyeg.tajr.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.greyeg.tajr.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkTimeTodayActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_today);
    }
}
