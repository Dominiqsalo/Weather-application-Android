package com.example.myweatherapp.ui;

public class WeatherDay {
    private String day;
    private String condition;
    private int temperature;

    public WeatherDay(String day, String condition, int temperature) {
        this.day = day;
        this.condition = condition;
        this.temperature = temperature;
    }

    // Getters for the variables:
    public String getDay() {
        return day;
    }

    public String getCondition() {
        return condition;
    }

    public int getTemperature() {
        return temperature;
    }

}
