package com.ake.locationweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ake.locationweather.Weather.WeatherMain;


/**
 * Created by 유현석 on 2017-03-31.
 */

public class WeatherFragment extends Fragment {
    private ImageView mImageMain;

    private TextView mSunRise;
    private TextView mSunrSet;

    private TextView mWindSpeed;
    private ImageView mWindDirection;

    private TextView mWeather;
    private TextView mTemperature;

    private TextView mAtmosphere;
    private TextView mHumidity;
    private TextView mVisualrange;

    private WeatherMain mModel;

    public static WeatherFragment newInstance(WeatherMain model) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        fragment.setArguments(bundle);
        return fragment;
    }

    public WeatherFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_frame, container, false);

        Bundle bundle = getArguments();
        mModel = (WeatherMain) bundle.getSerializable("model");

        mImageMain = (ImageView) view.findViewById(R.id.weaher_image_main);
        switch (mModel.getWeather().get(0).getMain()) {
            case "Clear":
                mImageMain.setImageResource(R.drawable.sun);
                break;
            case "Clouds":
                mImageMain.setImageResource(R.drawable.clouds);
                break;
            case "Snow":
                mImageMain.setImageResource(R.drawable.snow);
                break;
            case "Rain":
                mImageMain.setImageResource(R.drawable.rain);
                break;
            case "Haze":
                mImageMain.setImageResource(R.drawable.haze);
            case "Mist":
                mImageMain.setImageResource(R.drawable.mist);
                break;
        }

        mSunRise = (TextView) view.findViewById(R.id.weather_sunrise);
        mSunrSet = (TextView) view.findViewById(R.id.weather_sunset);

        mWindSpeed = (TextView) view.findViewById(R.id.weather_windspeed);
        mWindDirection = (ImageView) view.findViewById(R.id.weather_winddirection);

        mWeather = (TextView) view.findViewById(R.id.weatehr_weather);
        mTemperature = (TextView) view.findViewById(R.id.weather_temperature);

        mAtmosphere = (TextView) view.findViewById(R.id.weather_atmosphere);
        mHumidity = (TextView) view.findViewById(R.id.weather_humidity);
        mVisualrange = (TextView) view.findViewById(R.id.weather_visualrange);

        mSunRise.setText(mModel.getSys().getSunrise() + "");
        mSunrSet.setText(mModel.getSys().getSunset() + "");

        mWindSpeed.setText(mModel.getWind().getSpeed() + " m/s");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wind);
        mWindDirection.setImageBitmap(rotateImage(bitmap, mModel.getWind().getDeg()));

        mWeather.setText(mModel.getWeather().get(0).getDescription());
        mTemperature.setText(mModel.getMain().getTemp() + " ℃");

        mAtmosphere.setText(mModel.getMain().getPressure());
        mHumidity.setText(mModel.getMain().getHumidity() + " %");
        mVisualrange.setText(mModel.getVisibility() + " M");

        return view;
    }

    public Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}

