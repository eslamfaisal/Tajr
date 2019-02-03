package com.greyeg.tajr.activities;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.greyeg.tajr.R;
import com.greyeg.tajr.fragments.RequestBalanceFragment;
import com.greyeg.tajr.fragments.AvailableBalanceFragment;
import com.greyeg.tajr.fragments.BalanceRequestHistoryFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BalanceActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

   public static   Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        activity = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(this.getString(R.string.request_money), RequestBalanceFragment.class)
                .add(this.getString(R.string.balance_history), BalanceRequestHistoryFragment.class)
                .add(this.getString(R.string.availabl_balance), AvailableBalanceFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}
