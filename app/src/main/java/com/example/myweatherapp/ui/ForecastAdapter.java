package com.example.myweatherapp.ui;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myweatherapp.R;

import java.util.List;

// This adapter class provides data for a RecyclerView that displays a weekly weather forecast.
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    // List containing the weather data for the week.
    private List<WeatherDay> forecastList;
    private Context context;

    private String cityName;
    // Constructor that initializes the context and the forecast data.
    public ForecastAdapter(Context context, List<WeatherDay> forecastList,String cityName) {
        this.context = context;
        this.forecastList = forecastList;
        this.cityName= cityName;
        Log.d("DEBUG", "In Adapter Constructor: " + cityName);
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
        // Notify any other data change if required
        notifyDataSetChanged();
    }
    // This method creates and returns a new ViewHolder to represent an item in the RecyclerView.
    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_item, parent, false);
        return new ForecastViewHolder(view);
    }
    // This method binds data to a given ViewHolder. It sets the weather information on the provided ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        WeatherDay weatherDay = forecastList.get(position);
        holder.dayTextView.setText(weatherDay.getDay());
        int weatherIconRes = getWeatherIcon(weatherDay.getCondition());
        if (weatherIconRes != 0) {
            holder.weatherIconImageView.setImageResource(weatherIconRes);
            holder.weatherIconImageView.setVisibility(View.VISIBLE);
        } else {
            holder.weatherIconImageView.setVisibility(View.GONE);
        }
        holder.temperatureTextView.setText(String.format("%d°C", weatherDay.getTemperature()));
    }
    private int getWeatherIcon(String condition) {

        switch (condition.toLowerCase()) {
            case "clear":
                return R.drawable.icon_sun;  // Note: Ensure the drawable name is correct.
            case "clouds":
                return R.drawable.icon_cloud;
            case "rain":
                return R.drawable.icon_rain;
            case "drizzle":
                return R.drawable.icon_driz;
            case "thunderstorm":
                return R.drawable.icon_thun;
            case "atmosphere":
                return R.drawable.icon_atm;
            case "snow":
                return R.drawable.icon_snow;
            default:
                return 0;
        }
    }
    // This method returns the total number of items in the dataset.
    @Override
    public int getItemCount() {
        return forecastList.size();
    }
    // This nested class represents a single item in the RecyclerView. It contains views that display a day's weather information.
    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        ImageView weatherIconImageView;
        TextView temperatureTextView;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views within the layout and store references to them.
            dayTextView = itemView.findViewById(R.id.tv_day);
            weatherIconImageView = itemView.findViewById(R.id.iv_weather_icon);
            temperatureTextView = itemView.findViewById(R.id.tv_temperature);
        }
    }
    public void updateData(List<WeatherDay> newForecastItems) {
        this.forecastList = newForecastItems;
        notifyDataSetChanged();
    }
}
