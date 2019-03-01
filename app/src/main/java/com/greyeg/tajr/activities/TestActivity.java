package com.greyeg.tajr.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.greyeg.tajr.R;
import com.greyeg.tajr.sheets.TopSheetBehavior;

import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
//        Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
//        mProgressBar.setIndeterminateDrawable(getProgressDrawable());
//        mProgressBar.getIndeterminateDrawable().setBounds(bounds);
//
//        mProgressBar2.setIndeterminateDrawable(getProgressDrawable2());
//        mProgressBar2.getIndeterminateDrawable().setBounds(bounds);
//
//        mProgressBar3.setIndeterminateDrawable(getProgressDrawable3());
//        mProgressBar3.getIndeterminateDrawable().setBounds(bounds);
//
//
//
//        mProgressBar4.setIndeterminateDrawable(getProgressDrawable4());
//        mProgressBar4.getIndeterminateDrawable().setBounds(bounds);
        View sheet = findViewById(R.id.top_sheet);
        TopSheetBehavior tt = TopSheetBehavior.from(sheet);
        tt.setState(TopSheetBehavior.STATE_EXPANDED);
        tt.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                Log.d("TAG", "newState: " + newState);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset, Boolean isOpening) {
                Log.d("TAG", "slideOffset: " + slideOffset);
                if (isOpening != null) {
                    Log.d("TAG", "isOpening: " + isOpening);
                }
            }
        });

    }


}
