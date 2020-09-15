package com.example.inclass14;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    static MainActivity ctx;
    ArrayList<TripDetails> tripDetails  = new ArrayList<>();

    public MyAdapter(final ArrayList<TripDetails> tripDetails, MainActivity mainActivity) {
        this.tripDetails = new ArrayList<>(tripDetails);
        this.ctx = mainActivity;
        Collections.reverse(this.tripDetails);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_map;
        ImageView iv_add;
        TextView tv_tripNo;
        TextView tv_city;
        RecyclerView subRecyclerView;
        RecyclerView.Adapter rv_adapter;
        RecyclerView.LayoutManager rv_layoutManager;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_map = itemView.findViewById(R.id.iv_map);
            iv_add = itemView.findViewById(R.id.iv_add);
            tv_tripNo = itemView.findViewById(R.id.tv_tripNo);
            tv_city = itemView.findViewById(R.id.tv_city);
            subRecyclerView = itemView.findViewById(R.id.subRecyclerView);
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(rv_layout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_city.setText(tripDetails.get(position).city);
        holder.tv_tripNo.setText(tripDetails.get(position).trip);

        holder.iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()) {
                    if(null!=tripDetails.get(position).placesArrayList && tripDetails.get(position).placesArrayList.size() >0) {
                        Intent intent = new Intent(ctx, TripMapActivity.class);
                        intent.putExtra("Details", tripDetails.get(position).placesArrayList);
                        ctx.startActivity(intent);
                    }else{
                        Toast.makeText(ctx, "No places to display", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ctx, "No network connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx,AddPlacesActivity.class);
                intent.putExtra("Lat",tripDetails.get(position).lat);
                intent.putExtra("Lng",tripDetails.get(position).lng);
                intent.putExtra("DocumentID",tripDetails.get(position).documentID);
                ctx.startActivity(intent);
//                ctx.finish();
            }
        });
            holder.subRecyclerView.setVisibility(View.VISIBLE);
            holder.subRecyclerView.setHasFixedSize(true);
            holder.rv_layoutManager = new LinearLayoutManager(ctx);
            holder.subRecyclerView.setLayoutManager(holder.rv_layoutManager);
        if(null!= tripDetails.get(position).placesArrayList && tripDetails.get(position).placesArrayList.size() >0) {
            holder.rv_adapter = new MySubAdapter(tripDetails.get(position).placesArrayList, ctx, tripDetails.get(position));
            holder.subRecyclerView.setAdapter(holder.rv_adapter);
        }

    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }        return true;
    }

    @Override
    public int getItemCount() {
        return tripDetails.size();
    }
}
