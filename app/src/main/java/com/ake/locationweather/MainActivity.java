package com.ake.locationweather;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ake.locationweather.Forecast.ForecastMain;
import com.ake.locationweather.Weather.WeatherMain;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, MenuItem.OnMenuItemClickListener, SearchView.OnQueryTextListener {

    private Geocoder mGeocoder;
    private GoogleMap mMap;

    private MapApi mApi;
    private List<Address> mList;

    private MenuItem searchItem;
    private MenuItem searchClear;
    private SearchView searchView;

    private Marker mMarker;
    private double mLat;
    private double mLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(MapApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = mRetrofit.create(MapApi.class);

        mGeocoder = new Geocoder(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dial, menu);
        searchItem = menu.findItem(R.id.seach_item);
        searchClear = menu.findItem(R.id.clear_item);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchClear.setOnMenuItemClickListener(this);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        if (mLat == 0.0) {
            LatLng startingPoint = new LatLng(37.56, 126.97);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 10));
        } else {
            Quarterly();
        }

    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        mLat = latLng.latitude;
        mLng = latLng.longitude;

        Quarterly();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        citySearch(query);

        if (mList.size() == 0) {
            Toast.makeText(MainActivity.this, "재입력 하세요.", Toast.LENGTH_SHORT).show();
        } else {
            mLat = mList.get(0).getLatitude();
            mLng = mList.get(0).getLongitude();

            LatLng latLng = new LatLng(mLat, mLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

            Quarterly();

            // 키보드 숨기기
            InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            hide.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
        return true;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void citySearch(String city) {
        try {
            mList = mGeocoder.getFromLocationName(
                    city, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (mList != null) {
            if (mList.size() == 0) {
                Toast.makeText(this, "해당되는 주소정보는 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Quarterly() {
        final LatLng latLng = new LatLng(mLat, mLng);

        Call<WeatherMain> weatherCall = mApi.getWeaherData(MapApi.API_KEY, mLat, mLng);
        weatherCall.enqueue(new Callback<WeatherMain>() {
            @Override
            public void onResponse(final Call<WeatherMain> call, Response<WeatherMain> response) {
                final WeatherMain weatherMain = response.body();

                if (mMarker != null) {
                    mMarker.remove();
                }
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(weatherMain.getSys().getSunrise() + " ▶ " + weatherMain.getSys().getSunset()));
                mMarker.showInfoWindow();
                mMarker.hideInfoWindow();

                final Call<ForecastMain> forecastCall = mApi.getForecastData(MapApi.API_KEY, mLat, mLng);
                forecastCall.enqueue(new Callback<ForecastMain>() {
                    @Override
                    public void onResponse(Call<ForecastMain> call, Response<ForecastMain> response) {
                        final ForecastMain forecastMain = response.body();
                        GoogleMap.OnInfoWindowClickListener gMap = new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                                intent.putExtra("weather", weatherMain);
                                intent.putExtra("forecast", forecastMain);

                                startActivity(intent);
                            }
                        };
                        mMap.setOnInfoWindowClickListener(gMap);
                    }

                    @Override
                    public void onFailure(Call<ForecastMain> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<WeatherMain> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (mMarker != null) {
            mMarker.remove();
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble("Lat", mLat);
        outState.putDouble("Lng", mLng);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mLat = savedInstanceState.getDouble("Lat");
        mLng = savedInstanceState.getDouble("Lng");

    }
}
