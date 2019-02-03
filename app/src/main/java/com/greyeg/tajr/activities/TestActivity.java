package com.greyeg.tajr.activities;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.greyeg.tajr.R;
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.GoogleMusicDicesDrawable;
import com.jpardogo.android.googleprogressbar.library.NexusRotationCrossDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.progress_bar2)
    ProgressBar mProgressBar2;
    @BindView(R.id.progress_bar3)
    ProgressBar mProgressBar3;
    @BindView(R.id.progress_bar5)
    ProgressBar mProgressBar4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
        mProgressBar.setIndeterminateDrawable(getProgressDrawable());
        mProgressBar.getIndeterminateDrawable().setBounds(bounds);

        mProgressBar2.setIndeterminateDrawable(getProgressDrawable2());
        mProgressBar2.getIndeterminateDrawable().setBounds(bounds);

        mProgressBar3.setIndeterminateDrawable(getProgressDrawable3());
        mProgressBar3.getIndeterminateDrawable().setBounds(bounds);



        mProgressBar4.setIndeterminateDrawable(getProgressDrawable4());
        mProgressBar4.getIndeterminateDrawable().setBounds(bounds);


    }
    private Drawable getProgressDrawable() {
        Drawable progressDrawable = null;
                progressDrawable = new FoldingCirclesDrawable.Builder(this)
                        .colors(getProgressDrawableColors())
                        .build();

        return progressDrawable;
    }
    private Drawable getProgressDrawable2() {
        Drawable progressDrawable = null;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    private Drawable getProgressDrawable3() {
        Drawable progressDrawable = null;
        progressDrawable = new NexusRotationCrossDrawable.Builder(this)
                .colors(getProgressDrawableColors())
                .build();

        return progressDrawable;
    }
    private Drawable getProgressDrawable4() {
        Drawable progressDrawable = null;
        progressDrawable = new ChromeFloatingCirclesDrawable.Builder(this)
                .colors(getProgressDrawableColors())
                .build();

        return progressDrawable;
    }

    private int[] getProgressDrawableColors() {
        int[] colors = new int[4];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        colors[0] =getResources().getColor(R.color.red);
        colors[1] = getResources().getColor(R.color.blue);
        colors[2] = getResources().getColor(R.color.yellow);
        colors[3] = getResources().getColor(R.color.green);
        return colors;
    }

    void setUpProgressBar(){
        int[] colors = new int[4];
        colors[0] =getResources().getColor(R.color.red);
        colors[1] = getResources().getColor(R.color.blue);
        colors[2] = getResources().getColor(R.color.yellow);
        colors[3] = getResources().getColor(R.color.green);
        Drawable  progressDrawable = new ChromeFloatingCirclesDrawable.Builder(this)
                .colors(colors)
                .build();
        Rect bounds = mProgressBar.getIndeterminateDrawable().getBounds();
        mProgressBar.setIndeterminateDrawable(progressDrawable);
        mProgressBar.getIndeterminateDrawable().setBounds(bounds);

    }

}
