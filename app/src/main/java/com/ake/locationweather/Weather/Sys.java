package com.ake.locationweather.Weather;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 유현석 on 2017-03-17.
 */

public class Sys implements Serializable {
    private String message;

    private String id;

    private double sunset;

    private double sunrise;

    private String type;

    private String country;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSunset() {
        return UTC(sunset);
    }

    public void setSunset(double sunset) {
        this.sunset = sunset;
    }

    public String getSunrise() {
        return UTC(sunrise);
    }

    public void setSunrise(double sunrise) {
        this.sunrise = sunrise;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", id = " + id + ", sunset = " + sunset + ", sunrise = " + sunrise + ", type = " + type + ", country = " + country + "]";
    }

    public String UTC(double sun) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.KOREA);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(sun * 1000L);
    }
}
