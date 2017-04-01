package com.ake.locationweather;

import com.ake.locationweather.Forecast.ForecastMain;
import com.ake.locationweather.Weather.WeatherMain;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 유현석 on 2017-03-31.
 */

public interface MapApi {
    String API_KEY = "f483fcc0ddd60cbcc4fb17c53e81cd1c";
    String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @GET("weather")
    Call<WeatherMain> getWeaherData(@Query("APPID") String appId,
                                    @Query("lat") double lat,
                                    @Query("lon") double lon);

    @GET("forecast")
    Call<ForecastMain> getForecastData(@Query("APPID") String appId,
                                       @Query("lat") double lat,
                                       @Query("lon") double lon);
}
