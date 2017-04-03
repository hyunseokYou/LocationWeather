package com.ake.locationweather;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ake.locationweather.Forecast.ForecastMain;
import com.ake.locationweather.Weather.WeatherMain;

/**
 * Created by 유현석 on 2017-03-31.
 */

public class DialogActivity extends AppCompatActivity implements View.OnClickListener{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Button mButton;
    private WeatherFragment mFragmentWeather;
    private ForecastFragment mFragmentForeCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        mFragmentWeather = WeatherFragment.newInstance((WeatherMain) getIntent().getSerializableExtra("weather"));
        mFragmentForeCast = ForecastFragment.newInstance((ForecastMain) getIntent().getSerializableExtra("forecast"));

        mTabLayout = (TabLayout) findViewById(R.id.dialog_tab);
        mViewPager = (ViewPager) findViewById(R.id.dialog_pager);

        mButton = (Button) findViewById(R.id.dialog_button);


        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mFragmentWeather;
                case 1:
                    return mFragmentForeCast;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0 :
                    return "현재 날씨";
                case 1 :
                    return "날씨 정보";
            }
            return null;
        }
    }
}
