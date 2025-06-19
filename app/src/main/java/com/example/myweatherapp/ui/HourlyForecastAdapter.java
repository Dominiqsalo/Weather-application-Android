package com.example.myweatherapp.ui;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.myweatherapp.R;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>{

    private Context context;
    private List<WeatherHour> hourlyData;
    private String cityName;
    public HourlyForecastAdapter(Context context, List<WeatherHour> hourlyData, String cityName) {
        this.context = context;
        this.hourlyData = hourlyData;
        this.cityName = cityName;

    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
        // Notify any other data change if required
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hourly_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherHour hour = hourlyData.get(position);
        holder.timeTextView.setText(hour.getTime());
        holder.conditionTextView.setText(hour.getCondition());
        holder.tempTextView.setText(String.format("%s°C",hour.getTemperature()));
        ImageView weatherIcon = holder.itemView.findViewById(R.id.weatherIcon);

        String condition = hour.getCondition();// get the weather condition for the current item
        switch(condition.toLowerCase()) {
            case "clouds":
                weatherIcon.setImageResource(R.drawable.cloudy);
                break;
            case "clear":
                weatherIcon.setImageResource(R.drawable.sunny);
                break;
            case "rain":
                weatherIcon.setImageResource(R.drawable.rainy);
                break;
            case "snow":
                weatherIcon.setImageResource(R.drawable.snowy);
                break;
            default:
                weatherIcon.setVisibility(View.GONE);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the detail activity or fragment here
                Log.d("DEBUG", "Inside Click: " + cityName);
                Intent intent = new Intent(view.getContext(), DetailedWeatherActivity.class);
                Log.d("DEBUG", "City Name: " + cityName);
                intent.putExtra("cityName", cityName); // You can pass any data you need for the detailed view
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hourlyData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView conditionTextView;
        TextView tempTextView;
        ImageView weatherIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            timeTextView = itemView.findViewById(R.id.text_time);
            conditionTextView = itemView.findViewById(R.id.text_condition);
            tempTextView = itemView.findViewById(R.id.text_temp);
        }
    }
    public void updateData(List<WeatherHour> newForecastItems) {
        this.hourlyData = newForecastItems;
        notifyDataSetChanged();
    }
}
