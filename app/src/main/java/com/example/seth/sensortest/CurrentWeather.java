package com.example.seth.sensortest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Seth on 2/25/16.
 */
public class CurrentWeather {
    private String[] mIcon = new String[7];
    private long[] mTime = new long[7];
    private int mTemperature;
    private int[] mLowTemp = new int[7];
    private int[] mHighTemp = new int[7];
    private String mTimeZone;

    public void setTime(long mTime, int index) {
        this.mTime[index] = mTime;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double mTemperature) {
        this.mTemperature = (int) Math.round(mTemperature);
    }

    public int[] getLowTemp() {
        return mLowTemp;
    }

    public void setLowTemp(double mLowTemp, int index) {
        this.mLowTemp[index] = (int) Math.round(mLowTemp);
    }

    public int[] getHighTemp() {
        return mHighTemp;
    }

    public void setHighTemp(double mHighTemp, int index) {
        this.mHighTemp[index] = (int) Math.round(mHighTemp);
    }

    public long[] getTime() {
        return mTime;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String mTimeZone) {
        this.mTimeZone = mTimeZone;
    }

    public String[] getIcon() {

        return mIcon;
    }

    public void setIcon(String mIcon, int index) {
        this.mIcon[index] = mIcon;
    }

    public int getIconId(int index) {
        int iconId = R.drawable.clear_day;

        if (mIcon[index].equals("clear-day")) {
            iconId = R.drawable.clear_day;
        } else if (mIcon[index].equals("clear-night")) {
            iconId = R.drawable.clear_night;
        } else if (mIcon[index].equals("rain")) {
            iconId = R.drawable.rain;
        } else if (mIcon[index].equals("snow")) {
            iconId = R.drawable.snow;
        } else if (mIcon[index].equals("sleet")) {
            iconId = R.drawable.sleet;
        } else if (mIcon[index].equals("wind")) {
            iconId = R.drawable.wind;
        } else if (mIcon[index].equals("fog")) {
            iconId = R.drawable.fog;
        } else if (mIcon[index].equals("cloudy")) {
            iconId = R.drawable.cloudy;
        } else if (mIcon[index].equals("partly-cloudy-day")) {
            iconId = R.drawable.cloudy_day;
        } else if (mIcon[index].equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }

    public String getFormattedTime(int index) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE.");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime()[index] * 1000);
        String timeString = formatter.format(dateTime);
        return timeString;
    }
}