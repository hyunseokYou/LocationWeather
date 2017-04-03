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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ake.locationweather.Forecast.ForecastMain;
import com.ake.locationweather.Forecast.List;

import java.util.ArrayList;

/**
 * Created by 유현석 on 2017-03-31.
 */

public class ForecastFragment extends Fragment{

    private ForecastMain mModel;
    private ExpandableListView mListView;

    public ForecastFragment() {
    }

    public static ForecastFragment newInstance(ForecastMain model) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forecast_fragment, container, false);

        Bundle bundle = getArguments();
        mModel = (ForecastMain) bundle.getSerializable("model");

        mListView = (ExpandableListView) view.findViewById(R.id.list_exp);

        MyAdapter adapter = new MyAdapter((ArrayList<List>) mModel.getList());
        mListView.setAdapter(adapter);

        return view;
    }


    public class MyAdapter extends BaseExpandableListAdapter {

        private ArrayList<List> mParentList;

        public MyAdapter(ArrayList<List> mParentList) {
            this.mParentList = mParentList;
        }

        @Override
        public int getGroupCount() {
            return mParentList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mParentList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mParentList.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            groupViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new groupViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info, parent, false);
                viewHolder.mParentText = (TextView) convertView.findViewById(R.id.text_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (groupViewHolder) convertView.getTag();
            }
            viewHolder.mParentText.setText(mModel.getList().get(groupPosition).getDtTxt());

            return convertView;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            childViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new childViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_frame, parent, false);

                viewHolder.mWeather = (TextView) convertView.findViewById(R.id.forecast_weather);
                viewHolder.mImageMain = (ImageView) convertView.findViewById(R.id.forecast_image);
                viewHolder.mTemperText = (TextView) convertView.findViewById(R.id.forecast_temper);
                viewHolder.mSpeedText = (TextView) convertView.findViewById(R.id.forecast_speed);
                viewHolder.mImageWay = (ImageView) convertView.findViewById(R.id.forecast_way);
                viewHolder.mAtmoText = (TextView) convertView.findViewById(R.id.forecast_atmo);
                viewHolder.mHumText = (TextView) convertView.findViewById(R.id.forecast_hum);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (childViewHolder) convertView.getTag();
            }

            switch (mModel.getList().get(groupPosition).getWeather().get(childPosition).getMain()) {
                case "Clear":
                    viewHolder.mImageMain.setImageResource(R.drawable.sun);
                    break;
                case "Clouds":
                    viewHolder.mImageMain.setImageResource(R.drawable.clouds);
                    break;
                case "Snow":
                    viewHolder.mImageMain.setImageResource(R.drawable.snow);
                    break;
                case "Rain":
                    viewHolder.mImageMain.setImageResource(R.drawable.rain);
                    break;
                case "Haze":
                    viewHolder.mImageMain.setImageResource(R.drawable.haze);
                case "Mist":
                    viewHolder.mImageMain.setImageResource(R.drawable.mist);
                    break;
            }

            viewHolder.mWeather.setText(mModel.getList().get(groupPosition).getWeather().get(0).getDescription());
            viewHolder.mTemperText.setText(mModel.getList().get(groupPosition).getMain().getTemp() + " ℃");
            viewHolder.mSpeedText.setText(mModel.getList().get(groupPosition).getWind().getSpeed() + " m/s");
            viewHolder.mAtmoText.setText(mModel.getList().get(groupPosition).getMain().getPressure());
            viewHolder.mHumText.setText(mModel.getList().get(groupPosition).getMain().getHumidity() + " %");


            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wind);
            viewHolder.mImageWay.setImageBitmap(rotateImage(bitmap, mModel.getList().get(groupPosition).getWind().getDeg()));

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private class groupViewHolder {
            TextView mParentText;
        }

        private class childViewHolder {
            TextView mWeather;
            TextView mTemperText;
            TextView mSpeedText;
            ImageView mImageWay;
            TextView mAtmoText;
            TextView mHumText;
            ImageView mImageMain;
        }
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

