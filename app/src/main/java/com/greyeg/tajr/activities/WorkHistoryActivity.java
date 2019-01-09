package com.greyeg.tajr.activities;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.greyeg.tajr.R;
import com.greyeg.tajr.fragments.HoursFragment;
import com.greyeg.tajr.fragments.MoneyFragment;
import com.greyeg.tajr.fragments.NewOrderFragment;
import com.greyeg.tajr.fragments.PointsFragment;
import com.greyeg.tajr.fragments.SearchOrderPhoneFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WorkHistoryActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_history);
        getSupportActionBar().setTitle(getString(R.string.analytics));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(this.getString(R.string.money), MoneyFragment.class)
                .add(this.getString(R.string.points), PointsFragment.class)
                .add(this.getString(R.string.hours), HoursFragment.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
//
//    PointsFragment
//            fragment_points
}
