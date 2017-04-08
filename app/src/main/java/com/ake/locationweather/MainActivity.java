package com.ake.locationweather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ake.locationweather.Forecast.ForecastMain;
import com.ake.locationweather.Weather.WeatherMain;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, MenuItem.OnMenuItemClickListener,
        SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener {

    private Geocoder mGeocoder;
    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;

    private MapApi mApi;

    private List<Address> mList;
    private MenuItem mSearch, mSearchClear, mShare;

    private SearchView searchView;
    private Marker mMarker;
    private double mLat, mLng;
    private Location mLastLocation;
    private float mZoom = 10f;
    private String mPhoneNumber;
    private String mSunrise;
    private String mSunset;

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

        mGeocoder = new Geocoder(this, Locale.KOREA);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dial, menu);
        mShare = menu.findItem(R.id.share_item);
        mSearch = menu.findItem(R.id.seach_item);
        mSearchClear = menu.findItem(R.id.clear_item);
        searchView = (SearchView) MenuItemCompat.getActionView(mSearch);
        mShare.setOnMenuItemClickListener(this);
        mSearchClear.setOnMenuItemClickListener(this);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
        }
        mMap.setOnMapLongClickListener(this);
        if (mLat != 0.0) {
            quarterly();
        }
    }


    @Override
    public void onMapLongClick(final LatLng latLng) {
        mLat = latLng.latitude;
        mLng = latLng.longitude;

        quarterly();
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
            if (mZoom != 10f) {
                mZoom = mMap.getCameraPosition().zoom;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoom));

            quarterly();

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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_item:
                if (mMarker != null) {
                    mMarker.remove();
                }
                break;
            case R.id.share_item:
                    phoneNumBerPick();
        }
        return false;
    }

    public void phoneNumBerPick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_number, null, false);
        TextView numText = (TextView) view.findViewById(R.id.num_text);
        numText.setText(getAddress(mLat, mLng) + " 의 정보를 공유하시겠습니까?");
        final EditText numEditText = (EditText) view.findViewById(R.id.num_edit);
        builder.setView(view);
        builder.setPositiveButton("전송", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPhoneNumber = numEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mPhoneNumber));
                intent.putExtra("sms_body", messageFormat());
                startActivity(intent);
            }

        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    public String getAddress(double lat, double lng) {
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        List<Address> address;
        try {
            if (mGeocoder != null) {
                address = mGeocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    currentLocationAddress = currentLocationAddress.substring(5, currentLocationAddress.length());
                    nowAddress = currentLocationAddress;
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, "주소를 가져 올 수 없습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return nowAddress;
    }

    public String messageFormat() {
        final String patten = "일출 : {0}, 일몰 : {1}\n지도에서 보기 : http://maps.google.com/maps?q={2},{3}";
        return MessageFormat.format(patten, new Object[]{mSunrise, mSunset, mLat, mLng});
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

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.connect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLat = mLastLocation.getLatitude();
            mLng = mLastLocation.getLongitude();
            if (mZoom != 10f) {
                mZoom = mMap.getCameraPosition().zoom;
            }
            LatLng startingPoint = new LatLng(mLat, mLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, mZoom));
            quarterly();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;

            case 1001:
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (mMarker != null) {
            mMarker.remove();
        }
        mLat = mLastLocation.getLatitude();
        mLng = mLastLocation.getLongitude();
        LatLng startingPoint = new LatLng(mLat, mLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, mZoom));

        quarterly();
        return false;
    }

    public void quarterly() {
        final LatLng latLng = new LatLng(mLat, mLng);

        Call<WeatherMain> weatherCall = mApi.getWeaherData(MapApi.API_KEY, mLat, mLng);
        weatherCall.enqueue(new Callback<WeatherMain>() {
            @Override
            public void onResponse(final Call<WeatherMain> call, Response<WeatherMain> response) {
                final WeatherMain weatherMain = response.body();

                if (mMarker != null) {
                    mMarker.remove();
                }
                mSunrise = weatherMain.getSys().getSunrise();
                mSunset = weatherMain.getSys().getSunset();

                mMarker = mMap.addMarker(new MarkerOptions().position(latLng).
                        title(mSunrise + " ▶ " + mSunset));
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
                                intent.putExtra("adress", getAddress(mLat, mLng));
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


}
