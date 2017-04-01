package com.ake.locationweather.Weather;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by 유현석 on 2017-03-17.
 */

public class Main implements Serializable {
    private String humidity;

    private String pressure;

    private String temp_max;

    private String temp_min;

    private double temp;

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getTemp() {
        temp -= 273.15;
        DecimalFormat form = new DecimalFormat("#.##");
        return form.format(temp);
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "ClassPojo [humidity = " + humidity + ", pressure = " + pressure + ", temp_max = " + temp_max + ", temp_min = " + temp_min + ", temp = " + temp + "]";
    }
}
