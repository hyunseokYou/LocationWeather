package com.ake.locationweather.Weather;

import java.io.Serializable;

/**
 * Created by 유현석 on 2017-03-17.
 */

public class Wind implements Serializable {
    private String speed;
    private float deg;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "speed='" + speed + '\'' +
                ", deg='" + deg + '\'' +
                '}';
    }
}
