package com.example.inclass14;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyCitiesAdapter extends RecyclerView.Adapter<MyCitiesAdapter.MyViewHolder> {


    static AddTripActivity ctx;
    private OnItemClickListener newOnItemClickListener;
    ArrayList<Cities> cities;

    public MyCitiesAdapter(ArrayList<Cities> cities, AddTripActivity addTripActivity) {
        this.cities = cities;
        this.ctx = addTripActivity;
        this.newOnItemClickListener = addTripActivity;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_cityName;
        OnItemClickListener onItemClickListener;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_cityName = itemView.findViewById(R.id.tv_cityName);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_recycler_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rv_layout,  newOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

         holder.tv_cityName.setText(cities.get(position).description);

    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }
}
