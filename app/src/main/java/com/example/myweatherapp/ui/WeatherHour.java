package com.example.myweatherapp.ui;

public class WeatherHour {
    private String time;
    private String condition;
    private int temperature;

    public WeatherHour(String time, String condition, int temperature) {
        this.time = time;
        this.condition = condition;
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public String getCondition() {
        return condition;
    }

    public int getTemperature() {
        return temperature;
    }
}
