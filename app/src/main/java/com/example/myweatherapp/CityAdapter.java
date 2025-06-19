package com.example.myweatherapp;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myweatherapp.ui.WeatherDisplayActivity;
import com.example.myweatherapp.ui.home.HomeFragment;

import java.util.List;
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder>{

    private List<String> cityList;
    private Context context;
    private CityItemClickListener cityItemClickListener;

    public CityAdapter(Context context, List<String> cityList, CityItemClickListener listener) {
        this.context = context;
        this.cityList = cityList;
        this.cityItemClickListener = listener;

    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view, cityItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        String cityName = cityList.get(position);
        holder.cityName.setText(cityName);
        holder.itemView.setOnClickListener(v -> {
            // Fetch hourly data for the clicked city
            cityItemClickListener.onCityClick(cityName);
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void removeItem(int position) {
        cityList.remove(position);
        notifyItemRemoved(position);
    }
    class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityName;
        Button deleteCityButton;
        public CityViewHolder(@NonNull View itemView, CityItemClickListener listener) {
            super(itemView);
            cityName = itemView.findViewById(R.id.textViewCityName);
            deleteCityButton = itemView.findViewById(R.id.deleteCityButton);

            itemView.setOnClickListener(v ->  {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String city = cityList.get(position);
                        listener.onCityClick(city);

                        Intent intent = new Intent(context, WeatherDisplayActivity.class);
                        intent.putExtra("CITY_NAME", city);
                        context.startActivity(intent);
                    }
                }
            });
            deleteCityButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String city = cityList.get(position);
                        removeItem(position);
                        listener.onDeleteCityClick(city);
                    }
                }
            });
        }
    }
    public interface CityItemClickListener {
        void onCityClick(String cityName);
        void onDeleteCityClick(String cityName);

    }


}
