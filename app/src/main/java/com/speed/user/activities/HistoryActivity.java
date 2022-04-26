package com.speed.user.activities;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.speed.user.R;
import com.speed.user.fragments.OnGoingTrips;
import com.speed.user.fragments.PastTrips;
import com.google.android.material.tabs.TabLayout;
import com.speed.user.helper.SharedHelper;

public class HistoryActivity extends AppCompatActivity {
    ImageView backArrow;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String[] tabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        backArrow = findViewById(R.id.backArrow);

        if (SharedHelper.getKey(this, "selectedlanguage").contains("ar")) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            backArrow.setImageDrawable(getDrawable(R.drawable.ic_forward));

        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        backArrow.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
        String strTag = getIntent().getExtras().getString("tag");
        tabTitles = new String[]{getString(R.string.past_trips), getString(R.string.upcoming_rides)};

        viewPager.setAdapter(new SampleFragmentPagerAdapter(tabTitles, getSupportFragmentManager(),
                this));

        if (strTag != null) {
            if (strTag.equalsIgnoreCase("past")) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private String[] tabTitles;
        private Context context;

        public SampleFragmentPagerAdapter(String[] tabTitles, FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
            this.tabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new OnGoingTrips();
                default:
                    return new PastTrips();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
